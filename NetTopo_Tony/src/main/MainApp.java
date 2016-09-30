package main;

import impl.MasterThread;

import org.deri.nettopo.app.NetTopoApp;

import member.Master;

public class MainApp {

	/**
	 * @param args
	 * @throws Exception
	 */
	public final static Master master = new Master();
	private static MasterThread mt = null;

	public static void main(String[] args) {
		try {
			run();
			NetTopoApp.getApp().run();
			System.out.println("OK");
		} catch (Exception e) {
			System.exit(1);
		}
	}

	public static void run() {
		mt = new MasterThread(master);
		Thread th = new Thread(mt);
		th.start();
	}

}
