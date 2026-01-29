package gui;

import java.awt.Color;


import java.awt.EventQueue;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
import java.awt.Font;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.Timer;
import com.formdev.flatlaf.FlatLightLaf;

import clases.Calendario;

import javax.swing.UIManager;


public class LoginAcceso extends JFrame  {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
    private JTextField txtUsuario;
    private JPasswordField jPass;
    private JLabel lblHora;
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("defaultFont", new Font("Montserrat", Font.PLAIN, 14)); 
        } catch (Exception ex) {
            System.err.println("No se pudo aplicar FlatLaf.");
        }

        EventQueue.invokeLater(() -> {
            try {
                LoginAcceso frame = new LoginAcceso();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public LoginAcceso() {
    	setTitle("Inicio de Sesion");
    	setIconImage(Toolkit.getDefaultToolkit().getImage(LoginAcceso.class.getResource("/recursos/icons8-lock-48.png")));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 512, 512);
        
        contentPane = new JPanel();
        
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel lblUsuario = new JLabel("USUARIO :");
        lblUsuario.setForeground(Color.WHITE);
        
        lblUsuario.setFont(new Font("Segoe UI Black", Font.PLAIN, 20));
        lblUsuario.setBounds(70, 134, 116, 25);
        contentPane.add(lblUsuario);

        txtUsuario = new JTextField();
        
        txtUsuario.setBounds(231, 128, 207, 31);
        contentPane.add(txtUsuario);
        txtUsuario.setColumns(10);

        JLabel lblClave = new JLabel("CONTRASE�A :");
        lblClave.setForeground(Color.WHITE);
        
        lblClave.setFont(new Font("Segoe UI Black", Font.PLAIN, 20));
        lblClave.setBounds(70, 194, 151, 25);
        contentPane.add(lblClave);

        jPass = new JPasswordField();
        
        jPass.setBounds(231, 188, 207, 31);
        contentPane.add(jPass);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.setBackground(new Color(0, 153, 255));
        btnIngresar.setBorderPainted(false);
        
        btnIngresar.setFont(new Font("Segoe UI Light", Font.BOLD, 20));
        btnIngresar.addActionListener(new ActionListener() {
        	//metodo de usuario y contraseña
        	public void actionPerformed(ActionEvent e) {
        		
        	    String user = getUsuario();
        	    String clave = getClave();

        	    //Usuarios y contraseñas permitidos 
        	    String[] usuarios = {"luisEspinoza", "antony", "danielMilla"};
        	    String[] claves = {"970163", "984198", "940281"};

        	    boolean acceso = false;
        	    String nombreUsuario = "";

        	    //Verificar si el usuario y la clave coinciden
        	    for (int i = 0; i < usuarios.length; i++) {
        	    	try {
        	        if (user.equals(usuarios[i]) && clave.equals(claves[i])) {
        	            acceso = true;
        	            nombreUsuario = usuarios[i];
        	            break;
        	        }
        	        }catch (Exception z) { 
        	        	System.out.println("Ingres� usuario y clave");
        	        	}
        	    }
        	    if (acceso) {
        	        //Formatear el nombre del usuario
        	        nombreUsuario = formatearNombre(nombreUsuario);

        	        JOptionPane.showMessageDialog(LoginAcceso.this,
        	            "�Acceso coincidido a: " + nombreUsuario + "!");
        	        
        	        try {
        	            UIManager.setLookAndFeel(new FlatLightLaf());
        	            UIManager.put("defaultFont", new Font("Montserrat", Font.PLAIN, 14));
        	        } catch (Exception ex) {
        	            System.err.println("No se pudo aplicar FlatLaf.");
        	        }

        	        FrmPrincipal frm = new FrmPrincipal();
        	        frm.setUndecorated(false); 
        	        frm.setLocationRelativeTo(null);
        	        frm.setVisible(true);
        	        frm.setLocationRelativeTo(null);
        	        dispose();
        	    } else {
        	        mensajeError("�Acceso denegado!");
        	        txtUsuario.setText("");
        	        jPass.setText("");
        	        txtUsuario.requestFocus();
        	    }
        	}

        	//Método para formatear el nombre
        	
        	private String formatearNombre(String usuario) {
        	    
        		//Convetir "luisEspinoza" a "Luis Espinoza"
        	    StringBuilder sb = new StringBuilder();
        	    for (int i = 0; i < usuario.length(); i++) {
        	        char c = usuario.charAt(i);
        	        if (i > 0 && Character.isUpperCase(c)) {
        	            sb.append(" ");
        	        }
        	        sb.append(c);
        	    }

        	    String[] partes = sb.toString().split(" ");
        	    StringBuilder nombreFormateado = new StringBuilder();
        	    for (String parte : partes) {
        	        if (!parte.isEmpty()) {
        	            nombreFormateado.append(Character.toUpperCase(parte.charAt(0)))
        	                            .append(parte.substring(1).toLowerCase())
        	                            .append(" ");
        	        }
        	    }
        	    return nombreFormateado.toString().trim();
        	}});

        btnIngresar.setBounds(70, 244, 368, 40);
        contentPane.add(btnIngresar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(255, 102, 102));
        btnCancelar.setBorderPainted(false);
        
        btnCancelar.setFont(new Font("Segoe UI Light", Font.BOLD, 20));
        btnCancelar.setBounds(70, 309, 368, 40);
        btnCancelar.addActionListener(e -> System.exit(0));
        contentPane.add(btnCancelar);
        
        JLabel lblBienvenido = new JLabel("Ingrese Datos\r\n");
        lblBienvenido.setForeground(Color.WHITE);
        lblBienvenido.setHorizontalAlignment(SwingConstants.CENTER);
        
        lblBienvenido.setFont(new Font("Arial Black", Font.PLAIN, 35));
        lblBienvenido.setBounds(10, 39, 492, 40);
        contentPane.add(lblBienvenido);
        
        lblHora = new JLabel("");
        lblHora.setForeground(Color.WHITE);
        lblHora.setHorizontalAlignment(SwingConstants.CENTER);
        
        lblHora.setFont(new Font("Arial Black", Font.PLAIN, 50));
        
        lblHora.setBounds(10, 360, 492, 85);
        contentPane.add(lblHora);
        
      //Visualizar hora en tiempo real
        Timer timer = new Timer(1000, e -> actualizarHora());
        timer.start();
        
        JLabel lblFondo = new JLabel("");
        lblFondo.setIcon(new ImageIcon(LoginAcceso.class.getResource("/recursos/luilogin.gif")));
        lblFondo.setBounds(0, -12, 512, 535);
        contentPane.add(lblFondo);
    }

    private String getClave() {
        String pass = null;
        if (jPass.getPassword().length == 0) {
            mensajeError("Ingresar Clave");
            jPass.setText("");
            jPass.requestFocus();
        } else {
            pass = String.valueOf(jPass.getPassword());
        }
        return pass;
    }

    private String getUsuario() {
        String u = null;
        if (txtUsuario.getText().trim().length() == 0) {
            mensajeError("Ingresar Usuario");
            txtUsuario.setText("");
            txtUsuario.requestFocus();
        } else {
            u = txtUsuario.getText();
        }
        return u;
    }

    private void mensajeError(String msj) {
        JOptionPane.showMessageDialog(this, msj, "Aviso", JOptionPane.ERROR_MESSAGE);
    }
    
    //metodo de Actualizar hora
    public void actualizarHora() {
        lblHora.setText(Calendario.horaActual());
    }
}