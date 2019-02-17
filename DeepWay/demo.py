import cv2
import numpy as np
import os
import time
import datetime
from detectHuman import detect
from voice import Voice
from keras.preprocessing.image import img_to_array
from keras.models import load_model
from stop import Stop
from model.yolo_model import YOLO

def get_classes(file):
    """Get classes name.

    # Argument:
        file: classes name for database.

    # Returns
        class_names: List, classes name.

    """
    with open(file) as f:
        class_names = f.readlines()
    class_names = [c.strip() for c in class_names]

    return class_names

def draw(image, boxes, scores, classes, all_classes):
    """Draw the boxes on the image.

    # Argument:
        image: original image.
        boxes: ndarray, boxes of objects.
        classes: ndarray, classes of objects.
        scores: ndarray, scores of objects.
        all_classes: all classes name.
    """
    for box, score, cl in zip(boxes, scores, classes):
        x, y, w, h = box

        top = max(0, np.floor(x + 0.5).astype(int))
        left = max(0, np.floor(y + 0.5).astype(int))
        right = min(image.shape[1], np.floor(x + w + 0.5).astype(int))
        bottom = min(image.shape[0], np.floor(y + h + 0.5).astype(int))

        cv2.rectangle(image, (top, left), (right, bottom), (255, 0, 0), 2)
        cv2.putText(image, '{0} {1:.2f}'.format(all_classes[cl], score),
                    (top, left - 6),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.6, (0, 0, 255), 1,
                    cv2.LINE_AA)

        print('class: {0}, score: {1:.2f}'.format(all_classes[cl], score))
        print('box coordinate x,y,w,h: {0}'.format(box))

    print()

def process_image(img):
    """Resize, reduce and expand image.

    # Argument:
        img: original image.

    # Returns
        image: ndarray(64, 64, 3), processed image.
    """
    image = cv2.resize(img, (416, 416),
                       interpolation=cv2.INTER_CUBIC)
    image = np.array(image, dtype='float32')
    image /= 255.
    image = np.expand_dims(image, axis=0)

    return image

def detect_image(image, yolo, all_classes):
    """Use yolo v3 to detect images.

    # Argument:
        image: original image.
        yolo: YOLO, yolo model.
        all_classes: all classes name.

    # Returns:
        image: processed image.
    """
    pimage = process_image(image)

    start = time.time()
    boxes, classes, scores = yolo.predict(pimage, image.shape)
    end = time.time()

    print('time: {0:.2f}s'.format(end - start))

    if boxes is not None:
        draw(image, boxes, scores, classes, all_classes)

    return image

def detect_video(video, yolo, all_classes):
    """Use yolo v3 to detect video.

    # Argument:
        video: video file.
        yolo: YOLO, yolo model.
        all_classes: all classes name.
    """
    video_path = os.path.join("videos", "test", video)
    camera = cv2.VideoCapture(video_path)
    cv2.namedWindow("detection", cv2.WINDOW_AUTOSIZE)

    # Prepare for saving the detected video
    sz = (int(camera.get(cv2.CAP_PROP_FRAME_WIDTH)),
        int(camera.get(cv2.CAP_PROP_FRAME_HEIGHT)))
    fourcc = cv2.VideoWriter_fourcc(*'mpeg')

    vout = cv2.VideoWriter()
    vout.open(os.path.join("videos", "res", video), fourcc, 20, sz, True)

    while True:
        res, frame = camera.read()

        if not res:
            break

        image = detect_image(frame, yolo, all_classes)
        cv2.imshow("detection", image)

        # Save the video frame by frame
        vout.write(image)

        if cv2.waitKey(110) & 0xff == 27:
                break

    vout.release()
    camera.release()

def getListOfDic():
    path_dir = 'C:/xampp/htdocs/image'
    file_list = os.listdir(path_dir)
    file_list.sort()
    return file_list

def drawArrow(frame):
    f=frame[120:320,60:260]
    bg=cv2.bitwise_and(f,f,mask=arrow_mask_inv)
    fg=cv2.bitwise_and(arrow,arrow,mask=arrow_mask)
    final=cv2.bitwise_or(fg,bg)
    frame[120:320,60:260]=final
    return frame

i = 0
path = 'C:/xampp/htdocs/image/'
filelist = getListOfDic()
print(filelist)
videofile = path+filelist[i]
model_name='./model/multi_img_classification.model'
#out=cv2.VideoWriter('c.avi',cv2.VideoWriter_fourcc(*'MJPG'),20.0,(640, 480))
width=64
height=64
prob=0
label=''
isObstacle = False

arrow=cv2.imread('arrow.png',-1)
arrow=cv2.resize(arrow,(200,200))
arrow_mask=arrow[:,:,3:]
arrow_mask_inv=cv2.bitwise_not(arrow_mask)
arrow=arrow[:,:,:3]
yolo = YOLO(0.6, 0.5)
file = 'data/coco_classes.txt'
all_classes = get_classes(file)

print("loading model .....")
model=load_model(model_name)
print("model loaded")

vce = Voice()  # left(),right()
st = Stop()
current = datetime.datetime.now()
flag = None
cap = cv2.VideoCapture(videofile)
ret = True
prev = None

while ret:
    ret, frame = cap.read()
    frame = cv2.resize(frame, (640, 480))

    ##stop on left
    ##  you have a stop on '''
    current = datetime.datetime.now()
    new = current.second
    if ((current.second) % 4 == 0):
        if (prev != new):
            sto, direction = st.detect(frame)
            if (direction == 0):
                vce.stop_right()
            elif (direction == 1):
                vce.stop_left()

            bodys = detect_image(frame, yolo, all_classes)
            cv2.imshow('obstacleDetection', bodys)

            frame2 = frame
            frame2 = cv2.resize(frame2, (width, height))
            frame2 = frame2.astype('float32') / 255.0
            frame2 = img_to_array(frame2)
            frame2 = img_to_array(frame2)
            frame2 = np.expand_dims(frame2, axis=0)
            yhat = model.predict(frame2)
            left, right, center, obstacle = yhat[0]

            if (left > right and left > center):
                label = 'left'
                prob = left * 100
                # print(1)

            if (left < right and right > center):
                label = 'right'
                prob = right * 100
                # vce.left()
                # print(2)
                # frame=drawArrow(frame)
                #for i in range(1):
                    #ard.movleft()

            if (center > right and left < center):
                label = 'center'
                prob = center * 100
                # print(0)
                # vce.left()
                # frame=drawArrow(frame)
                #for i in range(1):
                    #ard.movleft()
            else:
                isObstacle = True
            prev = new
            isObstacle = False
    cv2.putText(frame, label + " with probability " + str(prob), (10, 25), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 0, 0), 2)
    if (label == 'center' or label == 'right'):
        frame = drawArrow(frame)
    cv2.imshow('frame', frame)
    i = i+1
    # out.write(frame)

    if (cv2.waitKey(1) & 0XFF == ord('q')):
        break
cap.release()
cv2.destroyAllWindows()