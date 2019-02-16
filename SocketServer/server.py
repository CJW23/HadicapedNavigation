import socket
import sys
import time

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind(('192.168.0.228', 91)) #ip주소와 포트 번호
server_socket.listen(0)

client_socket, addr = server_socket.accept()  # 소켓 허용
#예측된 결과를 전달하면 값을 어플로 전송.
while True:
    time.sleep(2)
    cmd = '1'  #예측 값
    b = bytes(cmd, 'utf-8') #데이터를 보낼 땐 byte로 변환해서 보내야 함
    client_socket.send(b)   #클라이언트(안드로이드)로 데이터를 보냄

    data = client_socket.recv(1024)  # data 받기
    if not data:
        break
    print(data)
client_socket.close()