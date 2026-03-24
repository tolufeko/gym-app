🌊 River Flow Prediction using MLP
This project implements a Multi-Layer Perceptron (MLP) from scratch to predict the mean daily flow of the River Ouse at Skelton, North Yorkshire. The model utilizes historical rainfall and flow data to provide one-day-ahead predictions, helping to analyze and forecast river behavior.

📊 Project Overview
The core of this project is a custom-built Neural Network designed to understand the relationship between precipitation at various upstream stations (Arkengarthdale, Malham Tarn, etc.) and the resulting river flow.

Key Features
Custom MLP Implementation: Hand-coded forward and backward propagation using NumPy.

Advanced Training: Includes momentum-based updates and weight decay to improve convergence and prevent overfitting.

Outlier Detection: Automated data cleaning using Interquartile Range (IQR) filtering.

Baseline Comparison: Performance benchmarking against a standard Linear Regression model.

Visualizations: Comprehensive plotting of actual vs. predicted flow and error metrics.

🛠 Tech Stack
Language: Python 3.10+

Numerical Computing: NumPy

Data Manipulation: Pandas, openpyxl

Machine Learning: Scikit-learn (for baseline comparison)

Visualization: Matplotlib, Seaborn

📂 File Structure
weather.py: The primary engine containing the MLP class, data preprocessing, and training logic.

header.cpp: C++ utility header used for [Insert Purpose].

Ouse93-96 - Student.xlsx: The source dataset containing rainfall and flow records (1993–1996).

.gitignore: Configured to exclude heavy binaries and environment files.

🚀 Getting Started
1. Installation
Clone the repository and install the required Python libraries:

Bash

git clone https://github.com/tolufeko/weather-flow-prediction.git
cd "ai methods"
pip install numpy pandas matplotlib seaborn scikit-learn openpyxl
2. Running the Model
Run the main script to process the data, train the MLP, and generate performance graphs:

Bash

python weather.py
📈 Model Architecture
The MLP is configured to minimize Mean Squared Error (MSE) through:

Input Layer: Previous day's rainfall and flow data.

Hidden Layer: Sigmoid activation units for non-linear mapping.

Output Layer: Single neuron predicting the next day's flow (Cumecs).

🔒 Academic Integrity
This repository is part of a University module for AI Methods.

Visibility: This project is intended for personal portfolio and grading purposes.

Ethics: Redistribution or copying of this code for similar academic assignments is strictly prohibited.