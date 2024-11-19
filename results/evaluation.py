import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from datetime import datetime
import re


# Extract offloading reasons
def extract_offloading_reasons(offloaded_by):
    reasons = re.findall(r":([A-Z_]+)", offloaded_by)
    return reasons


print("Loading data...")
FOLDER_NAME = "scenario-20k-FT-only-parent-concentrated"
df = pd.read_csv(f"{FOLDER_NAME}/results.csv")

# Convert timestamps to datetime
df["firstArrivalAtNode"] = pd.to_datetime(df["firstArrivalAtNode"])
df["executedAt"] = pd.to_datetime(df["executedAt"])
df["processing_time"] = (df["executedAt"] - df["firstArrivalAtNode"]).dt.total_seconds()
df["serviceDuration_sec"] = df["serviceDuration"] / 1000
df["total_execution_time"] = df["processing_time"] + df["serviceDuration_sec"]
df["ranking"] = ((df["userPriority"] + df["offloadCount"] / 2) * 2) + df[
    "servicePriority"
]
df["executed_in_cloud"] = df["offloadedBy"].apply(
    lambda x: 1 if "fog-node-7" in x else 0
)
df["executed_in_fog"] = df["offloadedBy"].apply(
    lambda x: 1 if "fog-node-7" not in x else 0
)
df["service"] = df["servicePriority"].apply(
    lambda x: "NEWS2" if x == 1.0 else "Random Forest Predictor"
)
df["executed_in_first_node"] = df["offloadCount"].apply(lambda x: 1 if x == 0 else 0)
df["offloading_reasons"] = df["offloadedBy"].apply(extract_offloading_reasons)

print("Data loaded successfully.")

df["time_from_start"] = (df["executedAt"] - df["executedAt"].min()).dt.total_seconds()
df["time_bin"] = pd.cut(df["time_from_start"], bins=60)

# Average offload count per time bin and priority
avg_offload = (
    df.groupby(["time_bin", "userPriority"], observed=False)["offloadCount"]
    .mean()
    .unstack()
)

# Prepare time bins as midpoints
bin_midpoints = [interval.mid for interval in avg_offload.index]
bin_times = pd.to_datetime(df["executedAt"].min()) + pd.to_timedelta(
    bin_midpoints, unit="s"
)
# Convert time bin midpoints to a new DataFrame for plotting
plot_df = avg_offload.reset_index()
plot_df["time_bin"] = bin_times

# Melt the DataFrame to a long format for seaborn
plot_df_melted = plot_df.melt(
    id_vars="time_bin", var_name="userPriority", value_name="avg_offload_count"
)

######## PLOTING MÉDIA DE OFFLOADING POR PRIORIDADE AO LONGO DO TEMPO ########

# Set theme
sns.set_theme(style="darkgrid")

# Plot
plt.figure(figsize=(14, 8))
sns.lineplot(
    data=plot_df_melted,
    x="time_bin",
    y="avg_offload_count",
    hue="userPriority",
    palette=sns.color_palette("crest", as_cmap=True),
    marker="o",
    markersize=6,
    linewidth=2,
)

# Customize title and labels
plt.title(
    "Média da Contagem de Offloading por Prioridade ao Longo do Tempo",
    fontsize=16,
    weight="bold",
    color="#333333",
)
plt.xlabel("Tempo (HH:MM)", fontsize=14, weight="bold", color="#555555")
plt.ylabel("Média da Contagem de Offloading", fontsize=14, weight="bold", color="#555555")

# Customize legend
plt.legend(
    title="Prioridade do Usuário",
    title_fontsize="13",
    fontsize="11",
    loc="upper left",
    frameon=True,
    shadow=True,
)

time_ticks = pd.date_range(
    start=plot_df_melted["time_bin"].min(),
    end=plot_df_melted["time_bin"].max(),
    freq="5min",
)
plt.xticks(time_ticks, time_ticks.strftime("%H:%M"), rotation=45, ha="right")
# Increase font size for tick labels
plt.tick_params(axis="x", labelsize=14)  # x-axis label size
plt.tick_params(axis="y", labelsize=14)  # y-axis label size
plt.tight_layout()
# plt.show()
plt.savefig(f"{FOLDER_NAME}/plots/avg_offloading_by_priority.png")
######## END PLOTING ########

####### PLOTTING TEMPO DE EXECUÇÃO POR PRIORIDADE ########
# Average processing time
avg_processing_time = (
    df.groupby(["time_bin", "userPriority"], observed=False)["processing_time"]
    .mean()
    .unstack()
)
bin_midpoints = [interval.mid for interval in avg_processing_time.index]
bin_times = pd.to_datetime(df["executedAt"].min()) + pd.to_timedelta(
    bin_midpoints, unit="s"
)
# Convert time bin midpoints to a new DataFrame for plotting
plot_df = avg_processing_time.reset_index()
plot_df["time_bin"] = bin_times

# Melt the DataFrame to a long format for seaborn
plot_df_melted = plot_df.melt(
    id_vars="time_bin", var_name="userPriority", value_name="avg_processing_time"
)

# Set theme
sns.set_theme(style="darkgrid")

# Plot
plt.figure(figsize=(14, 8))
sns.lineplot(
    data=plot_df_melted,
    x="time_bin",
    y="avg_processing_time",
    hue="userPriority",
    palette=sns.color_palette("crest", as_cmap=True),
    marker="o",
    markersize=6,
    linewidth=2,
)

# Customize title and labels
plt.title(
    "Tempo Médio até Execução (Excluindo o Tempo de Serviço) por Prioridade do Usuário ao Longo do Tempo",
    fontsize=16,
    weight="bold",
    color="#333333",
)
plt.xlabel("Tempo (HH:MM)", fontsize=14, weight="bold", color="#555555")
plt.ylabel(
    "Tempo Médio até Execução (Segundos)", fontsize=14, weight="bold", color="#555555"
)

# Customize legend
plt.legend(
    title="Prioridade do Usuário",
    title_fontsize="13",
    fontsize="11",
    loc="upper left",
    frameon=True,
    shadow=True,
)

time_ticks = pd.date_range(
    start=plot_df_melted["time_bin"].min(),
    end=plot_df_melted["time_bin"].max(),
    freq="5min",
)
plt.xticks(time_ticks, time_ticks.strftime("%H:%M"), rotation=45, ha="right")
# Increase font size for tick labels
plt.tick_params(axis="x", labelsize=14)  # x-axis label size
plt.tick_params(axis="y", labelsize=14)  # y-axis label size
plt.tight_layout()
# plt.show()
plt.savefig(f"{FOLDER_NAME}/plots/avg_processing_time_by_priority.png")


######## END PLOTING ########

####### PLOTTING OFFLOADING REASONS USER PRIORITY ########
df_exploded = df.explode("offloading_reasons")

count_data = (
    df_exploded.groupby(["userPriority", "offloading_reasons"])
    .size()
    .reset_index(name="count")
)

count_data["proportion"] = count_data.groupby("userPriority")["count"].transform(
    lambda x: x / x.sum()
)

pivot_data = count_data.pivot(
    index="userPriority", columns="offloading_reasons", values="count"
).fillna(0)

plt.figure(figsize=(14, 8))
sns.set_theme(style="darkgrid")

pivot_data.plot(kind="bar", stacked=True, figsize=(14, 8), colormap="crest")

plt.title(
    "Contagem de Motivos de Offloading por Prioridade de Usuário",
    fontsize=16,
    weight="bold",
    color="#333333",
)
plt.xlabel("Prioridade do Usuário", fontsize=14, weight="bold", color="#555555")
plt.ylabel(
    "Contagem de Motivos de Offloading", fontsize=14, weight="bold", color="#555555"
)
plt.xticks(
    rotation=0, fontsize=12, weight="bold", color="#555555"
)  # Adjust rotation and font

# Increase font size for tick labels
plt.tick_params(axis="x", labelsize=14)  # x-axis label size
plt.tick_params(axis="y", labelsize=14)  # y-axis label size

plt.legend(
    title="Motivo de Offloading",
    title_fontsize="13",
    fontsize="11",
    loc="upper right",
    frameon=True,
    shadow=True,
)

plt.tight_layout()
# plt.show()
plt.savefig(f"{FOLDER_NAME}/plots/offloading_reasons_per_priority.png")

########### END PLOTTING ########


########### PLOTTING SERVICE DURATIONS ########
avg_duration = (
    df.groupby(["time_bin", "service"], observed=False)["serviceDuration_sec"]
    .mean()
    .unstack()
)

# Prepare time bins as midpoints
bin_midpoints = [interval.mid for interval in avg_duration.index]
bin_times = pd.to_datetime(df["executedAt"].min()) + pd.to_timedelta(
    bin_midpoints, unit="s"
)
# Convert time bin midpoints to a new DataFrame for plotting
plot_df = avg_duration.reset_index()
plot_df["time_bin"] = bin_times

# Melt the DataFrame to a long format for seaborn
plot_df_melted = plot_df.melt(
    id_vars="time_bin", var_name="service", value_name="avg_duration"
)

# Set theme
sns.set_theme(style="darkgrid")

# Plot
plt.figure(figsize=(14, 8))
sns.lineplot(
    data=plot_df_melted,
    x="time_bin",
    y="avg_duration",
    hue="service",
    palette="crest",
    marker="o",
    markersize=6,
    linewidth=2,
)

# Customize title and labels
plt.title(
    "Média de Duração dos serviços ao Longo do Tempo",
    fontsize=16,
    weight="bold",
    color="#333333",
)
plt.xlabel("Tempo (HH:MM)", fontsize=14, weight="bold", color="#555555")
plt.ylabel("Média de Duração de Serviço (Segundos)", fontsize=14, weight="bold", color="#555555")

# Customize legend
plt.legend(
    title="Serviço",
    title_fontsize="13",
    fontsize="11",
    loc="upper left",
    frameon=True,
    shadow=True,
)

time_ticks = pd.date_range(
    start=plot_df_melted["time_bin"].min(),
    end=plot_df_melted["time_bin"].max(),
    freq="5min",
)
plt.xticks(time_ticks, time_ticks.strftime("%H:%M"), rotation=45, ha="right")
# Increase font size for tick labels
plt.tick_params(axis="x", labelsize=14)  # x-axis label size
plt.tick_params(axis="y", labelsize=14)  # y-axis label size
plt.tight_layout()
# plt.show()
plt.savefig(f"{FOLDER_NAME}/plots/avg_service_duration.png")
########### END PLOTTING ########

####### PLOTTING EXECUTION IN FOG VS CLOUD PER USER PRIORITY ########
execution_data = df.groupby(["userPriority"])[
    ["executed_in_fog", "executed_in_cloud"]
].sum()

# Plotting the stacked bar plot
plt.figure(figsize=(14, 8))
sns.set_theme(style="darkgrid")

# Create the stacked bar plot
execution_data.plot(kind="bar", stacked=True, figsize=(14, 8), colormap="crest")

# Title and labels
plt.title(
    "Contagem de Execuções por Prioridade do Usuário (Nuvem vs Fog)",
    fontsize=16,
    weight="bold",
    color="#333333",
)
plt.xlabel("Prioridade do Usuário", fontsize=14, weight="bold", color="#555555")
plt.ylabel("Contagem de Execuções", fontsize=14, weight="bold", color="#555555")

plt.xticks(rotation=0, fontsize=12)

handles, labels = plt.gca().get_legend_handles_labels()
custom_labels = ["Execução na Fog", "Execução na Nuvem"]

# Customizing the legend
plt.legend(
    handles=handles,
    labels=custom_labels,  # Use the custom labels here
    title="Local de Execução",
    title_fontsize="13",
    fontsize="11",
    loc="upper right",
    frameon=True,
    shadow=True,
    facecolor="white",  # Set the background color
    edgecolor="black",  # Optional: Set edge color for the legend box
    framealpha=0.5,  # Set transparency (0 is fully transparent, 1 is fully opaque)
)
# Increase font size for tick labels
plt.tick_params(axis="x", labelsize=14)  # x-axis label size
plt.tick_params(axis="y", labelsize=14)  # y-axis label size
# Tight layout
plt.tight_layout()
# plt.show()
plt.savefig(f"{FOLDER_NAME}/plots/execution_location_per_user_priority.png")
########### END PLOTTING ########

# Pivot the dataframe to structure the heatmap data
heatmap_data = df.pivot_table(
    index="service",  # y-axis
    columns="userPriority",  # x-axis
    values="total_execution_time",  # values for heatmap
    aggfunc="mean",  # Aggregating by mean in case of multiple entries
)

# Set up the matplotlib figure
plt.figure(figsize=(14, 8))

sns.heatmap(
    heatmap_data,
    annot=True,
    cmap="icefire",
    fmt=".2f",
    linewidths=0,  # No grid lines
    annot_kws={"weight": "bold"},  # Make annotations bold,
    vmin=heatmap_data.min().min(),  # Minimum value in your data
    vmax=heatmap_data.max().max(),  # Maximum value in your data
    cbar_kws={
        "label": "Tempo (segundos)",  # Color bar label
    },
)
# Increase font size for tick labels
plt.tick_params(axis="x", labelsize=14)  # x-axis label size
plt.tick_params(axis="y", labelsize=14)  # y-axis label size
plt.xlabel("Prioridade do Usuário", fontsize=14, weight="bold", color="#555555")
plt.ylabel("Serviço", fontsize=14, weight="bold", color="#555555")
plt.title(
    "Tempo Médio entre Chegada no Primeiro Nó e Finalização de Serviço por Prioridade do Usuário",
    fontsize=16,
    weight="bold",
    color="#333333",
)
plt.tight_layout()
plt.savefig(f"{FOLDER_NAME}/plots/heatmap_service_duration.png")
# plt.show()
