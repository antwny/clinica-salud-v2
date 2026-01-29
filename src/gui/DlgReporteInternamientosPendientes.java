package gui;

import arreglos.*;

//itext imports
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;

import clases.*;
import java.awt.Font;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import java.awt.Toolkit;

public class DlgReporteInternamientosPendientes extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton btnListar;
	private JTextArea txtResultado;
	private JScrollPane scrollPane;


	public static void main(String[] args) {
		try {
			DlgReporteInternamientosPendientes dialog = new DlgReporteInternamientosPendientes();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public DlgReporteInternamientosPendientes() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(DlgReporteInternamientosPendientes.class.getResource("/recursos/icons8-patient-64 (1).png")));
		getContentPane().setBackground(new Color(204, 208, 227));
		setTitle("Pacientes con Internamiento Pendiente");
		setBounds(100, 100, 640, 540);
		getContentPane().setLayout(null);
		
		btnListar = new JButton("Listar Pendientes");
		btnListar.setForeground(new Color(255, 255, 255));
		btnListar.setBackground(new Color(84, 163, 188));
		btnListar.addActionListener(this);
		btnListar.setFont(new Font("Segoe UI Black", Font.PLAIN, 14));
		btnListar.setBounds(230, 10, 180, 30);
		getContentPane().add(btnListar);
		
		JButton btnExportarPdf = new JButton("Exportar PDF");
		btnExportarPdf.setBounds(435, 11, 150, 30); // Ajusta la posición
		btnExportarPdf.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        generarPDF();
		    }
		});
		getContentPane().add(btnExportarPdf);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 50, 606, 440);
		getContentPane().add(scrollPane);
		
		txtResultado = new JTextArea();
		txtResultado.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtResultado.setEditable(false);
		scrollPane.setViewportView(txtResultado);
	}
	
	
	ArregloPacientes aa = new ArregloPacientes();
	
	private void generarPDF() {
	    JFileChooser chooser = new JFileChooser();
	    chooser.setDialogTitle("Guardar Reporte como PDF");
	    int selection = chooser.showSaveDialog(this);

	    if (selection == JFileChooser.APPROVE_OPTION) {
	        String ruta = chooser.getSelectedFile().getAbsolutePath();
	        if (!ruta.endsWith(".pdf")) ruta += ".pdf";

	        try {
	            Document doc = new Document();
	            PdfWriter.getInstance(doc, new FileOutputStream(ruta));
	            doc.open();
	            
	            // Título del PDF
	            doc.add(new Paragraph("CLÍNICA SALUD - REPORTE OFICIAL"));
	            doc.add(new Paragraph("--------------------------------------------------"));
	            
	            // Obtenemos el texto del JTextArea que ya llenaste con el botón Listar
	            doc.add(new Paragraph(txtResultado.getText()));

	            doc.close();
	            JOptionPane.showMessageDialog(this, "PDF generado con éxito en: " + ruta);
	        } catch (Exception e) {
	            JOptionPane.showMessageDialog(this, "Error al crear el PDF: " + e.getMessage());
	        }
	    }
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnListar) {
			actionPerformedBtnListar(e);
		}
	}
	
	protected void actionPerformedBtnListar(ActionEvent e) {
		txtResultado.setText("");
		
		imprimir("     REPORTE DE PACIENTES CON INTERNAMIENTO PENDIENTE");
		
		
		boolean hayPendientes = false;
		for (int i = 0; i < aa.tamanio(); i++) {
			if (aa.obtener(i).getEstado() == 0) { 
				hayPendientes = true;
				Paciente p = aa.obtener(i);
				imprimir(" CODIGO PACIENTE : " + p.getCodPaciente());
				imprimir(" NOMBRES          : " + p.getNombres());
				imprimir(" APELLIDOS        : " + p.getApellidos());
				imprimir(" DNI              : " + p.getDni());
				imprimir(" EDAD             : " + p.getEdad() + " años");
				imprimir(" CELULAR          : " + p.getCelular());
				imprimir(" ESTADO           : " + nombreEstado(p.getEstado()));
				imprimir("______________________________________________________________\n");
			}
		}
		
		if (!hayPendientes) {
			imprimir("No existen pacientes pendientes de internamiento.\n");
		}
	}
	
	
	String nombreEstado(int i) {
		switch (i) {
			case 0: return "REGISTRADO";
			case 1: return "INTERNADO";
			case 2: return "ALTA MEDICA";
			default: return "DESCONOCIDO";
		}
	}
	
	void imprimir(String s) {
		txtResultado.append(s + "\n");
	}
}
