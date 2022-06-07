package com.socket.exam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class TcpServer {

	/** 메인함수 정의 */
	public static void main(String[] args) {
		/** TcpServer 객체생성 및 doServer() 함수 실행 */
		new TcpServer().doServer();
	}



	public void doServer(){

		try {
			/** 서버소켓 객체생성 */
			ServerSocket socket = new ServerSocket(12225);
			System.out.println("서버시작");
			System.out.println(socket.getLocalSocketAddress());
			System.out.println(socket.getInetAddress());
			System.out.println(socket.getLocalPort());
			System.out.println("---------------------------------");

			while(true){
				/** 무한루프를 이용한 응답대기 */
				final Socket accept = socket.accept();

				/** 클라이언트 접속정보 조회 */
				synchronized(accept){
					InetAddress inetAddress = accept.getInetAddress();
					System.out.println("클라이언 접속 --------");
					System.out.println("getHostAddress = "+inetAddress.getHostAddress());
					System.out.println("getHostName = " + inetAddress.getHostName());
					System.out.println("getLocalHost = " + inetAddress.getLocalHost());
				}

				/** 통신을 위한 InputStream, OutputStream 객체 가져옴 */
				InputStream is = accept.getInputStream();
				OutputStream os = accept.getOutputStream();



				/** 서버 수신작업 쓰레드 분리 및 시작 */
				new Thread(){
					public void run(){
						/** InputStream → InputStreamReader → BufferedReader 객체생성 */
						BufferedReader br
							= new BufferedReader(new InputStreamReader(is));
						try {
							/** 자료수신 */
							while(true){
								/** 클라이언트가 송신한 자료를 수신한다. */
								String readLine = br.readLine();
								if(readLine==null) break;
								System.out.println("[서버화면] "+readLine);
							}
						} catch (IOException e) {
							e.printStackTrace();
							/** 수신에러 발생 시 클라이언트 통신 소켓종료 */
							try {
								accept.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}.start();

				/** 서버 송신작업 쓰레드 분리 및 시작 */
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
								/** 클라이언트로 자료를 송신한다. */
								bw.write("[1] S->C : "+date+"\r\n");
								bw.flush();
							}
						} catch (IOException e) {
							e.printStackTrace();
							/** 송신에러 발생 시 클라이언트 통신 소켓종료 */
							try {
								accept.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}