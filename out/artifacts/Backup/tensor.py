import subprocess
import tensorflow as tf
from tensorflow import keras
import numpy as np

sess = tf.Session()

def create_model():
    model = tf.keras.Sequential()
    model.add(layers.Dense(64, activation='relu'))
    model.add(layers.Dense(64, activation='relu'))
    model.add(layers.Dense(10, activation='softmax'))
    model.compile(optimizer=tf.train.AdamOptimizer(),
                  loss='mse',
                  metrics=['accuracy'])
    return model

model = create_model()
model.summary()

data = ''
labels = ''

val_data = ''
val_labels = ''

process = subprocess.Popen(['java', '-jar', 'Elastic-Optical-Network-Simulation.jar', '90', '75', '60', '40', '20', '10000', '1000', '120'], stdout=subprocess.PIPE)
out, err = process.communicate()
out_formatted = out.decode("utf-8")
print(out_formatted)

model.fit(data, labels, epochs=10, batch_size=32, validation_data=(val_data, val_labels))

model.evaluate(data, labels, batch_size=32)

result = model.predict(data, batch_size=32)
print(result.shape)
