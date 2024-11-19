from PIL import Image, ImageDraw, ImageFont
import numpy as np

# Define the folder and image files
FOLDER_NAME = "scenario-20k-FT-only-parent-concentrated"
image_files = [
    "CPU.png",
    "MEMORY.png",
    "LAMBDA_INVOCATIONS.png"
]
image_titles = ["CPU Usage", "Memory Usage", "Lambda Invocations"]  # Titles for the images
image_files = [FOLDER_NAME + "/" + image_file for image_file in image_files]

# Function to read and load images
def load_image(image_file):
    return Image.open(image_file)

# Function to create a blank canvas
def create_blank_canvas(width, height):
    return Image.new("RGB", (width, height), (255, 255, 255))

# Load and check the size of the first image to determine the canvas size
first_img = load_image(image_files[0])
image_width, image_height = first_img.size  # Get the actual size of the first image
canvas_width = image_width
canvas_height = (image_height + 30) * len(image_files)  # Add space for titles

# Create a blank canvas to combine images
canvas = create_blank_canvas(canvas_width, canvas_height)

# Function to paste images onto the canvas
def paste_image_on_canvas(canvas, image, row, col):
    canvas.paste(image, (col * image_width, row * (image_height + 30) + 30))  # Adjusted space for title

# Function to draw a title above the image
def draw_title_on_canvas(canvas, title, row):
    draw = ImageDraw.Draw(canvas)
    
    # Use the full path to the font file or use a built-in font
    font = ImageFont.truetype("/Library/Fonts/Arial.ttf", 26)  # Adjust path if necessary
    
    # Calculate text size using textbbox (bounding box)
    title_bbox = draw.textbbox((0, 0), title, font=font)
    title_width, title_height = title_bbox[2] - title_bbox[0], title_bbox[3] - title_bbox[1]
    
    # Center the title
    title_position = ((canvas_width - title_width) // 2, row * (image_height + 30))
    draw.text(title_position, title, font=font, fill=(0, 0, 0))

# Load and paste images with titles
for i, image_file in enumerate(image_files):
    img = load_image(image_file)
    img = img.resize((image_width, image_height))  # Resize if necessary
    row = i  # Determine row
    col = 0  # All images in a single column
    draw_title_on_canvas(canvas, image_titles[i], row)  # Draw title above the image
    paste_image_on_canvas(canvas, img, row, col)  # Paste image on canvas

# Save the combined image
canvas.save(FOLDER_NAME + "/plots/cloudwatch_with_titles.png", "PNG")
