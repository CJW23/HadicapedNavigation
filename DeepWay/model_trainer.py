from load_images import load
import random
import numpy as np
import keras
import tensorflow as tf

from keras.backend.tensorflow_backend import set_session
from keras.applications.vgg16 import VGG16
from sklearn.model_selection import train_test_split
from keras.utils import to_categorical
from keras.preprocessing.image import ImageDataGenerator
from keras.optimizers import Adam

config = tf.ConfigProto(
    gpu_options = tf.GPUOptions(per_process_gpu_memory_fraction=0.8),
    allow_soft_placement=True,
    log_device_placement=False
    # device_count = {'GPU': 1}
)
config.gpu_options.allow_growth = True
session = tf.Session(config=config)
set_session(session)

epochs = 1
learning_rate = 0.001
bs = 32
data = []
labels = []
model = VGG16(weights=None, include_top=True)
model.summary()
ld = load(224, 224, 3, [['D:/blind_project/left'], ['D:/blind_project/right'], ['D:/blind_project/center']])
data, labels = ld.imgload()
print(data)

c = list(zip(data, labels))
random.shuffle(c)

data[:], labels[:] = zip(*c)
data = np.array(data, dtype="float32")/255.0
labels = np.array(labels)
trainX, testX, trainY, testY = train_test_split(data, labels, test_size=0.1, random_state=42)
trainY = to_categorical(trainY, num_classes=1000)
testY = to_categorical(testY, num_classes=1000)
aug = ImageDataGenerator(rotation_range=30, width_shift_range=0.1, height_shift_range=0.1, shear_range=0.2,
                         zoom_range=0.2, fill_mode="nearest")
opt = Adam(lr=learning_rate, decay=learning_rate/epochs)

model.compile(loss="categorical_crossentropy", optimizer=opt, metrics=["accuracy"])

tb_hist = keras.callbacks.TensorBoard(log_dir='./graph', write_graph=True, write_images=True)
h = model.fit_generator(aug.flow(trainX, trainY, batch_size=bs), validation_data=(testX, testY),
                        steps_per_epoch=len(trainX)//bs, epochs=epochs, verbose=0, callbacks=[tb_hist])
model.save("blind_with_regularization.h5")

vgg16_feature = model.predict(testX)
print(vgg16_feature.shape)
print("success")
