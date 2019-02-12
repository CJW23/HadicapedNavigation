import cv2
import numpy as np
from matplotlib import pyplot as plt

class detect:
    def __init__(self):
        self.hog = cv2.HOGDescriptor()
        self.hog.setSVMDetector(cv2.HOGDescriptor_getDefaultPeopleDetector())

    '''
        def draw(self, rects, weights, frame):
            for i, (x, y, w, h) in enumerate(rects):
                if weights[i] < 0.7:
                    continue
            frame = cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
            return frame, (x + w)
    '''

    def humanDetect(self, frame):
        y = 0

        frame = cv2.resize(frame, (480, 640))
        grey = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        rects, weights = self.hog.detectMultiScale(grey)

        for i, (x, y, w, h) in enumerate(rects):
            if weights[i] < 0.5:
                continue
            frame = cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
            y = (x + w)

        if y == 0:
            return frame, -1
        elif y < 150:
            return frame, 1
        elif y >= 150:
            return frame, 0
