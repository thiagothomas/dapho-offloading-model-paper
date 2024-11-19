from PIL import Image
import numpy as np

# Define the folder and image files
FOLDER_NAME = "scenario-20k-FT-only-parent-concentrated"
image_files = [
    "avg_offloading_by_priority.png",
    "execution_location_per_user_priority.png",
    "avg_processing_time_by_priority.png",
    "offloading_reasons_per_priority.png",
    "avg_service_duration.png",
    "heatmap_service_duration.png",
]
image_files = [FOLDER_NAME + "/plots/" + image_file for image_file in image_files]


# Function to read and load images
def load_image(image_file):
    return Image.open(image_file)


# Function to create a blank canvas
def create_blank_canvas(width, height):
    return Image.new("RGB", (width, height), (255, 255, 255))


# Define the dimensions of the canvas and images
image_width, image_height = 1400, 800  # Adjust this according to the image sizes
canvas_width = image_width * 2  # 2 columns
canvas_height = image_height * 3  # 3 rows

# Create a blank canvas to combine images
canvas = create_blank_canvas(canvas_width, canvas_height)


# Function to paste images onto the canvas
def paste_image_on_canvas(canvas, image, row, col):
    canvas.paste(image, (col * image_width, row * image_height))


# Load and paste images
for i, image_file in enumerate(image_files):
    img = load_image(image_file)
    img = img.resize((image_width, image_height))  # Resize if necessary
    row = i // 2  # Determine row
    col = i % 2  # Determine column
    paste_image_on_canvas(canvas, img, row, col)

# Save the combined image
canvas.save(FOLDER_NAME + "/plots/combined_plots.png", "PNG")
