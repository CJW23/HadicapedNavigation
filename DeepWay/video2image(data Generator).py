import cv2

cap = cv2.VideoCapture('C:/Users/NestNetSSU/Desktop/test.mp4')
ret = True
i = 0
j = 3772
while cap.isOpened():
    ret, frame = cap.read()
    frame = cv2.resize(frame, (1280, 720))
    cv2.imshow('s', frame)
    if i % 5 == 0:
        cv2.imwrite('C:/Users/NestNetSSU/Desktop/test/'+str(j)+'.jpg', frame)
        j = j+1
    i = i+1

    if cv2.waitKey(1) & 0XFF == ord('q'):
        cap.release()
        break
cv2.destroyAllWindows()
