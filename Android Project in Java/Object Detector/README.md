# Object Detection App

This Android app uses TensorFlow Lite and the MobileNet model for object classification from images. It allows users to either capture an image using the camera or select an image from the gallery. After the image is selected or captured, the app predicts the object in the image using a pre-trained MobileNet model.

## Features

- **Image Selection**: Choose an image from the device's gallery.
- **Image Capture**: Capture an image using the device's camera.
- **Object Prediction**: Classifies the object in the selected or captured image using TensorFlow Lite's MobileNet model.
- **Result Display**: Displays the predicted class label on the screen.

## Prerequisites

To run this project, ensure you have the following prerequisites:

- Android Studio 4.1 or higher
- Android SDK 29 or higher
- Java 8 or higher
- TensorFlow Lite MobileNet model (mobilenet_quant_v1_224.tflite) and label file (`labels_mobilenet_quant_v1_224.txt`)
