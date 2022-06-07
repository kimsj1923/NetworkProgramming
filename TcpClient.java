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

			/** ����� ���� InputStream, OutputStream ��ü ������ */
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			/** Ŭ���̾�Ʈ �����۾� ������ �и� �� ���� */
			new Thread(){
				public void run(){
					/** InputStream �� InputStreamReader �� BufferedReader ��ü���� */
					BufferedReader br
						= new BufferedReader(new InputStreamReader(is));
					try {
						/** �ڷ���� */
						while(true){
							/** ������ �۽��� �ڷḦ �����Ѵ�. */
							String readLine = br.readLine();
							if(readLine==null) break;
							System.out.println("[Ŭ���̾�Ʈȭ��] "+readLine);
						}
					} catch (IOException e) {
						e.printStackTrace();
						/** ���ſ��� �߻� �� Ŭ���̾�Ʈ ��� ���� ���� */
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}.start();

			/** Ŭ���̾�Ʈ �۽��۾� ������ �и� �� ���� */
			new Thread(){
				public void run(){
					/** OutputStream �� OutputStreamWriter �� BufferedWriter ��ü���� */
					BufferedWriter bw
						= new BufferedWriter(new OutputStreamWriter(os));
					try {
						/** 3�ʸ��� �ð� ���� ���� �� �ݵ�� ���� �� ��\r\n���� ���� �� */
						while(true){
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							Date date = new Date(System.currentTimeMillis());
							/** ������ �ڷḦ �۽��Ѵ�. */
							bw.write("[2] C->S : "+date+"\r\n");
							bw.flush();
						}
					} catch (IOException e) {
						e.printStackTrace();
						/** �۽ſ��� �߻� �� Ŭ���̾�Ʈ ��� ���� ���� */
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