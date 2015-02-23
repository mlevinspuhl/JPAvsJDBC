package br.com.fiap.message;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JTextArea;

/**
 * 
 * @author Marcel Levinspuhl Junior
 * 
 *         classe que exibe as mensagens em tempo real
 * 
 */
public class ShowMessage implements Runnable {

	private JDialog dlg;
	private Thread th;
	private JTextArea jta;

	/**
	 * Este contrutor prepara o o JDialog recebendo apenas o título da janela
	 * 
	 * @param titulo
	 */
	public ShowMessage(String titulo) {

		dlg = new JDialog();
		dlg.setTitle(titulo);
		dlg.setEnabled(false);
		//define o comportamento do close da janela para destruir a janela e a thread
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		dlg.setLayout(null);
		
		dlg.getContentPane().setBackground(Color.WHITE);

		jta = new JTextArea();
		jta.setBounds(20, 20, 590, 490);
		Font font = new Font("Verdana", Font.BOLD, 13);
		jta.setFont(font);
		jta.setForeground(new Color(0, 0, 160));
		dlg.add(jta);
	}

	// aqui inicia a thread
	@Override
	public void run() {

		//Tamanho da janela
		dlg.setSize(600, 500);
		//Posiciona a janela no centro da tela
		dlg.setLocationRelativeTo(null);
		//torna a janela visível
		dlg.setVisible(true);

	}

	/**
	 * método que dispara a thread
	 */
	public void exec() {
		th = new Thread(this);
		th.start();
	}

	// /**
	// * método que destroy a thread
	// */
	// public void destroy(){
	//
	// if(th!=null && th.isAlive()){
	// th.interrupt();
	// }
	// th=null;
	// }
	//

	/**
	 * método que insere uma mensagem no JTextArea e revalida a GUI
	 * 
	 * @param message
	 */
	public void setMessage(String message) {

		jta.append(message);

		dlg.validate();
		dlg.repaint();

	}

}
