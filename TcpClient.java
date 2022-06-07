package com.socket.exam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class TcpClient {
	public static void main(String[] args) {
		new TcpClient().doClient();
	}

	public void doClient(){
		try {

			Socket socket = new Socket("localhost", 12225);

			/** 통신을 위한 InputStream, OutputStream 객체 가져옴 */
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			/** 클라이언트 수신작업 쓰레드 분리 및 시작 */
			new Thread(){
				public void run(){
					/** InputStream → InputStreamReader → BufferedReader 객체생성 */
					BufferedReader br
						= new BufferedReader(new InputStreamReader(is));
					try {
						/** 자료수신 */
						while(true){
							/** 서버가 송신한 자료를 수신한다. */
							String readLine = br.readLine();
							if(readLine==null) break;
							System.out.println("[클라이언트화면] "+readLine);
						}
					} catch (IOException e) {
						e.printStackTrace();
						/** 수신에러 발생 시 클라이언트 통신 소켓 종료 */
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}.start();

			/** 클라이언트 송신작업 쓰레드 분리 및 시작 */
			new Thread(){
				public void run(){
					/** OutputStream → OutputStreamWriter → BufferedWriter 객체생성 */
					BufferedWriter bw
						= new BufferedWriter(new OutputStreamWriter(os));
					try {
						/** 3초마다 시간 값을 전송 ☞ 반드시 보낼 때 『\r\n』을 붙일 것 */
						while(true){
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							Date date = new Date(System.currentTimeMillis());
							/** 서버로 자료를 송신한다. */
							bw.write("[2] C->S : "+date+"\r\n");
							bw.flush();
						}
					} catch (IOException e) {
						e.printStackTrace();
						/** 송신에러 발생 시 클라이언트 통신 소켓 종료 */
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}