package vista;

import dao.*;
import idao.ICrudDao;
import models.*;
import org.bson.Document;
import util.ExportadorUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

    private final RefugioDao refugioDao = new RefugioDao();
    private final AnimalDao animalDao = new AnimalDao();
    private final AdoptanteDao adoptanteDao = new AdoptanteDao();
    private final AdopcionDao adopcionDao = new AdopcionDao();
    private final MetadatosDao metadatosDao = new MetadatosDao();

    private JPanel panelPrincipal;
    private JTabbedPane tabbedPane;

    // Pestaña 1: Refugio
    private JTextField txtRefId, txtRefNombre, txtRefCif, txtRefFecha, txtRefDireccion, txtRefContacto;
    private JButton btnRefRecargar, btnRefGuardar;

    // Pestaña 2: Animales
    private JTextField txtAniId, txtAniNombre, txtAniEspecie, txtAniRaza, txtAniEdad, txtAniFechaIngreso, txtAniDescripcion;
    private JComboBox<String> cmbAniSexo, cmbAniEstado;
    private JCheckBox chkAniInactivos;
    private JButton btnAniLimpiar, btnAniAlta, btnAniModificar, btnAniBajaLogica, btnAniBajaFisica, btnAniHistorial;
    private JTable tablaAnimales;
    private DefaultTableModel modeloAnimales;

    // Pestaña 3: Adoptantes
    private JTextField txtAdoDni, txtAdoNombre, txtAdoApellidos, txtAdoTelefono, txtAdoDireccion, txtAdoEmail, txtAdoFechaRegistro, txtAdoPreferencias;
    private JCheckBox chkAdoInactivos;
    private JButton btnAdoLimpiar, btnAdoAlta, btnAdoModificar, btnAdoBajaLogica, btnAdoBajaFisica, btnAdoSolicitudes;
    private JTable tablaAdoptantes;
    private DefaultTableModel modeloAdoptantes;

    // Pestaña 4: Adopciones
    private JTextField txtAdopId, txtAdopIdAnimal, txtAdopDniAdoptante, txtAdopFechaSol, txtAdopFechaRes, txtAdopObservaciones;
    private JComboBox<String> cmbAdopEstado;
    private JCheckBox chkAdopInactivas;
    private JButton btnAdopLimpiar, btnAdopAlta, btnAdopModificar, btnAdopBajaLogica, btnAdopBajaFisica, btnAdopVerAsociados, btnAdopSeguimiento;
    private JTable tablaAdopciones;
    private DefaultTableModel modeloAdopciones;

    // Pestaña 5: Metadatos y Exportación
    private JButton btnExpAniJson, btnExpAniXml, btnExpAdoJson, btnExpAdoXml, btnExpAdopJson, btnExpAdopXml;
    private JTextArea txtAreaMetadatos;
    private JButton btnRefrescarMeta, btnExpMetaJson, btnExpMetaXml;

    private String idAnimalSeleccionado = null;
    private String dniAdoptanteSeleccionado = null;
    private String idAdopcionSeleccionada = null;

    @FunctionalInterface
    private interface AccionTresCampos {
        void ejecutar(String c1, String c2, String c3) throws Exception;
    }

    @FunctionalInterface
    private interface AccionExportar {
        void exportar(File destino) throws Exception;
    }

    public MainWindow() {
        setTitle("Gestión de Refugio de Animales - MongoDB");
        setContentPane(panelPrincipal);
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        registrarEventos();

        cargarDatosRefugio();
        cargarTablaAnimales();
        cargarTablaAdoptantes();
        cargarTablaAdopciones();
        cargarMetadatos();
    }

    private void inicializarComponentes() {
        cmbAniSexo.setModel(new DefaultComboBoxModel<>(new String[]{"Macho", "Hembra"}));
        cmbAniEstado.setModel(new DefaultComboBoxModel<>(new String[]{"disponible", "reservado", "adoptado", "en tratamiento"}));
        cmbAdopEstado.setModel(new DefaultComboBoxModel<>(new String[]{"pendiente", "aprobada", "rechazada"}));

        modeloAnimales = crearModeloNoEditable("ID", "Nombre", "Especie", "Raza", "Edad", "Sexo", "Fecha Ingreso", "Estado", "Activo", "Registros Médicos");
        tablaAnimales.setModel(modeloAnimales);

        modeloAdoptantes = crearModeloNoEditable("DNI/NIE", "Nombre", "Apellidos", "Teléfono", "Email", "Fecha Registro", "Preferencias", "Activo");
        tablaAdoptantes.setModel(modeloAdoptantes);

        modeloAdopciones = crearModeloNoEditable("ID Adopción", "ID Animal", "DNI Adoptante", "Fecha Solicitud", "Fecha Resolución", "Estado", "Activo", "Seguimientos");
        tablaAdopciones.setModel(modeloAdopciones);

        cmbAniSexo.setModel(new DefaultComboBoxModel<>(new String[]{"Macho", "Hembra"}));
        cmbAniSexo.setPrototypeDisplayValue("MMMMMMMMMM");

        cmbAniEstado.setModel(new DefaultComboBoxModel<>(new String[]{"disponible", "reservado", "adoptado", "en tratamiento"}));
        cmbAniEstado.setPrototypeDisplayValue("en tratamiento");

        cmbAdopEstado.setModel(new DefaultComboBoxModel<>(new String[]{"pendiente", "aprobada", "rechazada"}));
        cmbAdopEstado.setPrototypeDisplayValue("pendiente");

    }

    private void registrarEventos() {
        // Refugio
        btnRefRecargar.addActionListener(e -> cargarDatosRefugio());
        btnRefGuardar.addActionListener(e -> guardarDatosRefugio());

        // Animales
        vincularSeleccionTabla(tablaAnimales, this::rellenarFormularioAnimal);
        chkAniInactivos.addActionListener(e -> cargarTablaAnimales());
        btnAniLimpiar.addActionListener(e -> limpiarAnimales());
        btnAniAlta.addActionListener(e -> guardarAnimal(true));
        btnAniModificar.addActionListener(e -> guardarAnimal(false));
        btnAniBajaLogica.addActionListener(e -> bajaLogicaGenerica(animalDao, txtAniId.getText().trim(), "Animal", () -> {
            cargarTablaAnimales();
            limpiarAnimales();
        }));
        btnAniBajaFisica.addActionListener(e -> bajaFisicaGenerica(animalDao, txtAniId.getText().trim(), "Animal", () -> {
            cargarTablaAnimales();
            limpiarAnimales();
        }));
        btnAniHistorial.addActionListener(e -> abrirHistorialMedico());

        // Adoptantes
        vincularSeleccionTabla(tablaAdoptantes, this::rellenarFormularioAdoptante);
        chkAdoInactivos.addActionListener(e -> cargarTablaAdoptantes());
        btnAdoLimpiar.addActionListener(e -> limpiarAdoptantes());
        btnAdoAlta.addActionListener(e -> guardarAdoptante(true));
        btnAdoModificar.addActionListener(e -> guardarAdoptante(false));
        btnAdoBajaLogica.addActionListener(e -> bajaLogicaGenerica(adoptanteDao, txtAdoDni.getText().trim(), "Adoptante", () -> {
            cargarTablaAdoptantes();
            limpiarAdoptantes();
        }));
        btnAdoBajaFisica.addActionListener(e -> bajaFisicaGenerica(adoptanteDao, txtAdoDni.getText().trim(), "Adoptante", () -> {
            cargarTablaAdoptantes();
            limpiarAdoptantes();
        }));
        btnAdoSolicitudes.addActionListener(e -> verSolicitudesAdoptante());

        // Adopciones
        vincularSeleccionTabla(tablaAdopciones, this::rellenarFormularioAdopcion);
        chkAdopInactivas.addActionListener(e -> cargarTablaAdopciones());
        btnAdopLimpiar.addActionListener(e -> limpiarAdopciones());
        btnAdopAlta.addActionListener(e -> guardarAdopcion(true));
        btnAdopModificar.addActionListener(e -> guardarAdopcion(false));
        btnAdopBajaLogica.addActionListener(e -> bajaLogicaGenerica(adopcionDao, txtAdopId.getText().trim(), "Adopción", () -> {
            cargarTablaAdopciones();
            limpiarAdopciones();
        }));
        btnAdopBajaFisica.addActionListener(e -> bajaFisicaGenerica(adopcionDao, txtAdopId.getText().trim(), "Adopción", () -> {
            cargarTablaAdopciones();
            limpiarAdopciones();
        }));
        btnAdopVerAsociados.addActionListener(e -> verDatosAsociadosAdopcion());
        btnAdopSeguimiento.addActionListener(e -> abrirSeguimientoAdopcion());

        // Metadatos y Exportación
        btnExpAniJson.addActionListener(e -> exportarColeccion("animales", "JSON"));
        btnExpAniXml.addActionListener(e -> exportarColeccion("animales", "XML"));
        btnExpAdoJson.addActionListener(e -> exportarColeccion("adoptantes", "JSON"));
        btnExpAdoXml.addActionListener(e -> exportarColeccion("adoptantes", "XML"));
        btnExpAdopJson.addActionListener(e -> exportarColeccion("adopciones", "JSON"));
        btnExpAdopXml.addActionListener(e -> exportarColeccion("adopciones", "XML"));
        btnRefrescarMeta.addActionListener(e -> cargarMetadatos());
        btnExpMetaJson.addActionListener(e -> exportarMetadatos("JSON"));
        btnExpMetaXml.addActionListener(e -> exportarMetadatos("XML"));
    }


    // MÉTODOS AUXILIARES GENÉRICOS (REUTILIZACIÓN DE LÓGICA)

    private DefaultTableModel crearModeloNoEditable(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void vincularSeleccionTabla(JTable tabla, java.util.function.IntConsumer alSeleccionar) {
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                alSeleccionar.accept(tabla.getSelectedRow());
            }
        });
    }

    private void limpiarCampos(JTable tabla, JTextField... campos) {
        for (JTextField c : campos) c.setText("");
        if (tabla != null) tabla.clearSelection();
    }

    private <T> void bajaLogicaGenerica(ICrudDao<T, String> dao, String id, String entidad, Runnable refrescar) {
        try {
            if (id.isEmpty()) {
                mostrarAviso("Seleccione un registro de " + entidad + " de la tabla.");
                return;
            }
            dao.deleteLogico(id);
            refrescar.run();
            mostrarInfo(entidad + " dado de baja lógicamente.");
        } catch (Exception e) {
            mostrarError("Error en la baja lógica: " + e.getMessage());
        }
    }

    private <T> void bajaFisicaGenerica(ICrudDao<T, String> dao, String id, String entidad, Runnable refrescar) {
        try {
            if (id.isEmpty()) {
                mostrarAviso("Seleccione un registro de " + entidad + " de la tabla.");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "¿Eliminar permanentemente " + entidad + " (" + id + ")?",
                    "Confirmar Baja Física", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dao.deleteFisico(id);
                refrescar.run();
                mostrarInfo(entidad + " eliminado definitivamente.");
            }
        } catch (Exception e) {
            mostrarError("Error en la baja física: " + e.getMessage());
        }
    }

    private void abrirDialogoSubregistros(String titulo, String lblCampo2, String lblCampo3,
                                          java.util.concurrent.Callable<String> cargarTexto,
                                          AccionTresCampos alGuardar, Runnable refrescarTabla) {
        try {
            JDialog dialogo = new JDialog(this, titulo, true);
            dialogo.setSize(520, 380);
            dialogo.setLocationRelativeTo(this);
            dialogo.setLayout(new BorderLayout(10, 10));

            JTextArea area = new JTextArea(cargarTexto.call());
            area.setEditable(false);

            boolean tieneTercerCampo = (lblCampo3 != null);
            JPanel panelNuevo = new JPanel(new GridLayout(tieneTercerCampo ? 4 : 3, 2, 5, 5));
            JTextField txt1 = new JTextField();
            JTextField txt2 = new JTextField();
            JTextField txt3 = new JTextField();
            JButton btnAgregar = new JButton("Añadir");

            panelNuevo.add(new JLabel("Fecha (AAAA-MM-DD):"));
            panelNuevo.add(txt1);
            panelNuevo.add(new JLabel(lblCampo2));
            panelNuevo.add(txt2);
            if (tieneTercerCampo) {
                panelNuevo.add(new JLabel(lblCampo3));
                panelNuevo.add(txt3);
            }
            panelNuevo.add(new JLabel(""));
            panelNuevo.add(btnAgregar);

            btnAgregar.addActionListener(ev -> {
                try {
                    if (txt1.getText().trim().isEmpty() || txt2.getText().trim().isEmpty()) {
                        mostrarAviso("Los campos principales son obligatorios.");
                        return;
                    }
                    alGuardar.ejecutar(txt1.getText().trim(), txt2.getText().trim(), txt3.getText().trim());
                    area.setText(cargarTexto.call());
                    limpiarCampos(null, txt1, txt2, txt3);
                    refrescarTabla.run();
                    mostrarInfo("Registro añadido correctamente.");
                } catch (Exception ex) {
                    mostrarError("Error al guardar: " + ex.getMessage());
                }
            });

            dialogo.add(new JScrollPane(area), BorderLayout.CENTER);
            dialogo.add(panelNuevo, BorderLayout.SOUTH);
            dialogo.setVisible(true);
        } catch (Exception e) {
            mostrarError("Error al abrir ventana: " + e.getMessage());
        }
    }

    private void ejecutarExportacion(String nombreArchivo, AccionExportar accion) {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(nombreArchivo));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            File destino = chooser.getSelectedFile();
            accion.exportar(destino);
            mostrarInfo("Archivo exportado correctamente en:\n" + destino.getAbsolutePath());
        } catch (Exception e) {
            mostrarError("Error al exportar: " + e.getMessage());
        }
    }


    // PESTAÑA 1: REFUGIO

    private void cargarDatosRefugio() {
        try {
            Refugio r = refugioDao.obtenerRefugio();
            if (r != null) {
                txtRefId.setText(r.getId());
                txtRefNombre.setText(r.getNombre());
                txtRefCif.setText(r.getCif());
                txtRefFecha.setText(r.getFechaApertura());
                txtRefDireccion.setText(r.getDireccion());
                txtRefContacto.setText(r.getContacto());
            }
        } catch (Exception e) {
            mostrarError("Error al cargar refugio: " + e.getMessage());
        }
    }

    private void guardarDatosRefugio() {
        try {
            if (txtRefNombre.getText().trim().isEmpty() || txtRefCif.getText().trim().isEmpty()) {
                mostrarAviso("El nombre y el CIF son obligatorios.");
                return;
            }
            refugioDao.guardarOActualizar(new Refugio(
                    txtRefId.getText().trim(), txtRefNombre.getText().trim(), txtRefCif.getText().trim(),
                    txtRefFecha.getText().trim(), txtRefDireccion.getText().trim(), txtRefContacto.getText().trim()));
            mostrarInfo("Datos del refugio guardados.");
        } catch (Exception e) {
            mostrarError("Error al guardar refugio: " + e.getMessage());
        }
    }


    // PESTAÑA 2: ANIMALES E HISTORIAL MÉDICO

    private void cargarTablaAnimales() {
        try {
            modeloAnimales.setRowCount(0);
            for (Animal a : animalDao.findAll(chkAniInactivos.isSelected())) {
                modeloAnimales.addRow(new Object[]{
                        a.getIdAnimal(), a.getNombre(), a.getEspecie(), a.getRaza(),
                        a.getEdad(), a.getSexo(), a.getFechaIngreso(), a.getEstado(),
                        a.isActivo() ? "Sí" : "No", a.getHistorialMedico().size()
                });
            }
        } catch (Exception e) {
            mostrarError("Error al cargar animales: " + e.getMessage());
        }
    }

    private void rellenarFormularioAnimal(int fila) {

        try {
            Animal a = animalDao.findById(modeloAnimales.getValueAt(fila, 0).toString());
            if (a != null) {
                txtAniId.setText(a.getIdAnimal());
                txtAniNombre.setText(a.getNombre());
                txtAniEspecie.setText(a.getEspecie());
                txtAniRaza.setText(a.getRaza());
                txtAniEdad.setText(String.valueOf(a.getEdad()));
                cmbAniSexo.setSelectedItem(a.getSexo());
                txtAniDescripcion.setText(a.getDescripcion());
                txtAniFechaIngreso.setText(a.getFechaIngreso());
                cmbAniEstado.setSelectedItem(a.getEstado());
                idAnimalSeleccionado = a.getIdAnimal();
                txtAniId.setText(a.getIdAnimal());
                txtAniId.setEditable(false);
            }
        } catch (Exception e) {
            mostrarError("Error al seleccionar animal: " + e.getMessage());
        }
    }

    private void limpiarAnimales() {
        limpiarCampos(tablaAnimales, txtAniId, txtAniNombre, txtAniEspecie, txtAniRaza, txtAniEdad, txtAniDescripcion, txtAniFechaIngreso);
        cmbAniSexo.setSelectedIndex(0);
        cmbAniEstado.setSelectedIndex(0);
        idAnimalSeleccionado = null;
        txtAniId.setEditable(true);
        if (chkAniInactivos.isSelected()) {
            chkAniInactivos.setSelected(false);
            cargarTablaAnimales();
        }
    }

    private void guardarAnimal(boolean esAlta) {
        try {
            String id = esAlta ? txtAniId.getText().trim() : idAnimalSeleccionado;
            String nombre = txtAniNombre.getText().trim();

            if (!esAlta && (id == null || id.isEmpty())) {
                mostrarAviso("Seleccione un animal de la tabla para modificar.");
                return;
            }
            if (id == null || id.isEmpty() || nombre.isEmpty()) {
                mostrarAviso("El ID y el nombre del animal son obligatorios.");
                return;
            }

            int edad;
            try {
                edad = Integer.parseInt(txtAniEdad.getText().trim());
            } catch (NumberFormatException ex) {
                mostrarAviso("La edad debe ser un número entero.");
                return;
            }

            List<RegistroMedico> historial = new ArrayList<>();
            boolean activo = true;
            if (!esAlta) {
                Animal actual = animalDao.findById(id);
                if (actual == null) {
                    mostrarAviso("El animal seleccionado ya no existe en la base de datos.");
                    return;
                }
                historial = actual.getHistorialMedico();
                activo = actual.isActivo();
            }

            Animal animal = new Animal(id, nombre, txtAniEspecie.getText().trim(), txtAniRaza.getText().trim(),
                    edad, (String) cmbAniSexo.getSelectedItem(), txtAniDescripcion.getText().trim(),
                    txtAniFechaIngreso.getText().trim(), (String) cmbAniEstado.getSelectedItem(), activo, historial);

            if (esAlta) {
                animalDao.create(animal);
                limpiarAnimales();
                mostrarInfo("Animal registrado correctamente.");
            } else {
                animalDao.update(animal);
                mostrarInfo("Animal modificado correctamente.");
            }
            cargarTablaAnimales();
        } catch (Exception e) {
            mostrarError("Error al guardar animal: " + e.getMessage());
        }
    }

    private void abrirHistorialMedico() {
        String id = txtAniId.getText().trim();
        if (id.isEmpty()) {
            mostrarAviso("Seleccione primero un animal de la tabla.");
            return;
        }
        abrirDialogoSubregistros(
                "Historial Médico - Animal " + id,
                "Diagnóstico:",
                "Tratamiento:",
                () -> {
                    Animal a = animalDao.findById(id);
                    if (a == null || a.getHistorialMedico().isEmpty()) return "Sin registros médicos.";
                    StringBuilder sb = new StringBuilder();
                    for (RegistroMedico rm : a.getHistorialMedico()) {
                        sb.append("Fecha: ").append(rm.getFecha())
                                .append(" | Diagnóstico: ").append(rm.getDiagnostico())
                                .append(" | Tratamiento: ").append(rm.getTratamiento()).append("\n---\n");
                    }
                    return sb.toString();
                },
                (fecha, diag, trat) -> animalDao.agregarRegistroMedico(id, new RegistroMedico(fecha, diag, trat)),
                this::cargarTablaAnimales
        );
    }


    // PESTAÑA 3: ADOPTANTES

    private void cargarTablaAdoptantes() {
        try {
            modeloAdoptantes.setRowCount(0);
            for (Adoptante a : adoptanteDao.findAll(chkAdoInactivos.isSelected())) {
                modeloAdoptantes.addRow(new Object[]{
                        a.getDni(), a.getNombre(), a.getApellidos(), a.getTelefono(),
                        a.getEmail(), a.getFechaRegistro(), String.join(", ", a.getPreferencias()),
                        a.isActivo() ? "Sí" : "No"
                });
            }
        } catch (Exception e) {
            mostrarError("Error al cargar adoptantes: " + e.getMessage());
        }
    }

    private void rellenarFormularioAdoptante(int fila) {
        try {
            Adoptante a = adoptanteDao.findById(modeloAdoptantes.getValueAt(fila, 0).toString());
            if (a != null) {
                dniAdoptanteSeleccionado = a.getDni();
                txtAdoDni.setText(a.getDni());
                txtAdoDni.setEditable(false);
                txtAdoNombre.setText(a.getNombre());
                txtAdoApellidos.setText(a.getApellidos());
                txtAdoTelefono.setText(a.getTelefono());
                txtAdoDireccion.setText(a.getDireccion());
                txtAdoEmail.setText(a.getEmail());
                txtAdoFechaRegistro.setText(a.getFechaRegistro());
                txtAdoPreferencias.setText(String.join(", ", a.getPreferencias()));
            }
        } catch (Exception e) {
            mostrarError("Error al seleccionar adoptante: " + e.getMessage());
        }
    }

    private void limpiarAdoptantes() {
        dniAdoptanteSeleccionado = null;
        txtAdoDni.setEditable(true);
        limpiarCampos(tablaAdoptantes, txtAdoDni, txtAdoNombre, txtAdoApellidos, txtAdoTelefono, txtAdoDireccion, txtAdoEmail, txtAdoFechaRegistro, txtAdoPreferencias);
        if (chkAdoInactivos.isSelected()) {
            chkAdoInactivos.setSelected(false);
            cargarTablaAdoptantes();
        }
    }

    private void guardarAdoptante(boolean esAlta) {
        try {
            String dni = esAlta ? txtAdoDni.getText().trim() : dniAdoptanteSeleccionado;
            String nombre = txtAdoNombre.getText().trim();

            if (!esAlta && (dni == null || dni.isEmpty())) {
                mostrarAviso("Seleccione un adoptante de la tabla para modificar.");
                return;
            }
            if (dni == null || dni.isEmpty() || nombre.isEmpty()) {
                mostrarAviso("El DNI/NIE y el nombre son obligatorios.");
                return;
            }

            boolean activo = true;
            if (!esAlta) {
                Adoptante actual = adoptanteDao.findById(dni);
                if (actual == null) {
                    mostrarAviso("El adoptante seleccionado ya no existe en la base de datos.");
                    return;
                }
                activo = actual.isActivo();
            }

            List<String> prefs = new ArrayList<>();
            if (!txtAdoPreferencias.getText().trim().isEmpty()) {
                for (String p : txtAdoPreferencias.getText().trim().split(",")) {
                    if (!p.trim().isEmpty()) prefs.add(p.trim());
                }
            }

            Adoptante adoptante = new Adoptante(dni, nombre, txtAdoApellidos.getText().trim(), txtAdoTelefono.getText().trim(),
                    txtAdoDireccion.getText().trim(), txtAdoEmail.getText().trim(), txtAdoFechaRegistro.getText().trim(), activo, prefs);

            if (esAlta) {
                adoptanteDao.create(adoptante);
                limpiarAdoptantes();
                mostrarInfo("Adoptante registrado correctamente.");
            } else {
                adoptanteDao.update(adoptante);
                mostrarInfo("Adoptante modificado correctamente.");
            }
            cargarTablaAdoptantes();
        } catch (Exception e) {
            mostrarError("Error al guardar adoptante: " + e.getMessage());
        }
    }

    private void verSolicitudesAdoptante() {
        try {
            String dni = txtAdoDni.getText().trim();
            if (dni.isEmpty()) {
                mostrarAviso("Seleccione un adoptante de la tabla.");
                return;
            }
            List<Adopcion> lista = adopcionDao.findByAdoptante(dni);
            if (lista.isEmpty()) {
                mostrarInfo("Este adoptante no tiene solicitudes ni adopciones.");
                return;
            }
            StringBuilder sb = new StringBuilder("Solicitudes/Adopciones de " + dni + ":\n\n");
            for (Adopcion ad : lista) {
                sb.append("ID: ").append(ad.getIdAdopcion()).append(" | Animal: ").append(ad.getIdAnimal())
                        .append(" | Fecha: ").append(ad.getFechaSolicitud()).append(" | Estado: ").append(ad.getEstado()).append("\n");
            }
            mostrarInfo(sb.toString());
        } catch (Exception e) {
            mostrarError("Error al consultar solicitudes: " + e.getMessage());
        }
    }


    // PESTAÑA 4: ADOPCIONES Y SEGUIMIENTO

    private void cargarTablaAdopciones() {
        try {
            modeloAdopciones.setRowCount(0);
            for (Adopcion ad : adopcionDao.findAll(chkAdopInactivas.isSelected())) {
                modeloAdopciones.addRow(new Object[]{
                        ad.getIdAdopcion(), ad.getIdAnimal(), ad.getDniAdoptante(), ad.getFechaSolicitud(),
                        ad.getFechaResolucion() != null ? ad.getFechaResolucion() : "",
                        ad.getEstado(), ad.isActivo() ? "Sí" : "No", ad.getSeguimiento().size()
                });
            }
        } catch (Exception e) {
            mostrarError("Error al cargar adopciones: " + e.getMessage());
        }
    }

    private void rellenarFormularioAdopcion(int fila) {
        try {
            Adopcion ad = adopcionDao.findById(modeloAdopciones.getValueAt(fila, 0).toString());
            if (ad != null) {
                idAdopcionSeleccionada = ad.getIdAdopcion();
                txtAdopId.setText(ad.getIdAdopcion());
                txtAdopId.setEditable(false);
                txtAdopIdAnimal.setText(ad.getIdAnimal());
                txtAdopDniAdoptante.setText(ad.getDniAdoptante());
                txtAdopFechaSol.setText(ad.getFechaSolicitud());
                txtAdopFechaRes.setText(ad.getFechaResolucion() != null ? ad.getFechaResolucion() : "");
                cmbAdopEstado.setSelectedItem(ad.getEstado());
                txtAdopObservaciones.setText(ad.getObservaciones());
            }
        } catch (Exception e) {
            mostrarError("Error al seleccionar adopción: " + e.getMessage());
        }
    }

    private void limpiarAdopciones() {
        idAdopcionSeleccionada = null;
        txtAdopId.setEditable(true);
        limpiarCampos(tablaAdopciones, txtAdopId, txtAdopIdAnimal, txtAdopDniAdoptante, txtAdopFechaSol, txtAdopFechaRes, txtAdopObservaciones);
        cmbAdopEstado.setSelectedIndex(0);
        if (chkAdopInactivas.isSelected()) {
            chkAdopInactivas.setSelected(false);
            cargarTablaAdopciones();
        }
    }

    private void guardarAdopcion(boolean esAlta) {
        try {
            String id = esAlta ? txtAdopId.getText().trim() : idAdopcionSeleccionada;
            String idAni = txtAdopIdAnimal.getText().trim();
            String dni = txtAdopDniAdoptante.getText().trim();

            if (!esAlta && (id == null || id.isEmpty())) {
                mostrarAviso("Seleccione una adopción de la tabla para modificar.");
                return;
            }
            if (id == null || id.isEmpty() || idAni.isEmpty() || dni.isEmpty()) {
                mostrarAviso("ID Adopción, ID Animal y DNI Adoptante son obligatorios.");
                return;
            }

            List<Seguimiento> seg = new ArrayList<>();
            boolean activo = true;
            if (!esAlta) {
                Adopcion actual = adopcionDao.findById(id);
                if (actual == null) {
                    mostrarAviso("La adopción seleccionada ya no existe en la base de datos.");
                    return;
                }
                seg = actual.getSeguimiento();
                activo = actual.isActivo();
            }

            String fRes = txtAdopFechaRes.getText().trim();
            Adopcion adopcion = new Adopcion(id, idAni, dni, txtAdopFechaSol.getText().trim(),
                    fRes.isEmpty() ? null : fRes, (String) cmbAdopEstado.getSelectedItem(),
                    txtAdopObservaciones.getText().trim(), activo, seg);

            if (esAlta) {
                adopcionDao.create(adopcion);
                limpiarAdopciones();
                mostrarInfo("Solicitud de adopción registrada.");
            } else {
                adopcionDao.update(adopcion);
                mostrarInfo("Adopción actualizada correctamente.");
            }
            cargarTablaAdopciones();
            cargarTablaAnimales();
        } catch (Exception e) {
            mostrarError("Error al guardar adopción: " + e.getMessage());
        }
    }

    private void verDatosAsociadosAdopcion() {
        try {
            String idAni = txtAdopIdAnimal.getText().trim();
            String dni = txtAdopDniAdoptante.getText().trim();
            if (idAni.isEmpty() || dni.isEmpty()) {
                mostrarAviso("Seleccione una adopción de la tabla.");
                return;
            }
            Animal a = animalDao.findById(idAni);
            Adoptante ad = adoptanteDao.findById(dni);
            String txtA = (a != null) ? a.getIdAnimal() + " - " + a.getNombre() + " (" + a.getEspecie() + ")" : "No encontrado";
            String txtAd = (ad != null) ? ad.getDni() + " - " + ad.getNombre() + " " + ad.getApellidos() : "No encontrado";
            mostrarInfo("ANIMAL:\n" + txtA + "\n\nADOPTANTE:\n" + txtAd);
        } catch (Exception e) {
            mostrarError("Error al consultar asociados: " + e.getMessage());
        }
    }

    private void abrirSeguimientoAdopcion() {
        String id = txtAdopId.getText().trim();
        if (id.isEmpty()) {
            mostrarAviso("Seleccione una adopción de la tabla.");
            return;
        }
        abrirDialogoSubregistros(
                "Seguimiento Post-Adopción - " + id,
                "Notas / Observaciones:",
                null,
                () -> {
                    Adopcion ad = adopcionDao.findById(id);
                    if (ad == null || ad.getSeguimiento().isEmpty()) return "Sin registros de seguimiento.";
                    StringBuilder sb = new StringBuilder();
                    for (Seguimiento s : ad.getSeguimiento()) {
                        sb.append("Fecha: ").append(s.getFecha()).append(" | Notas: ").append(s.getNotas()).append("\n---\n");
                    }
                    return sb.toString();
                },
                (fecha, notas, ignorado) -> adopcionDao.agregarSeguimiento(id, new Seguimiento(fecha, notas)),
                this::cargarTablaAdopciones
        );
    }

    // PESTAÑA 5: METADATOS Y EXPORTACIÓN
    private void cargarMetadatos() {
        try {
            Document meta = metadatosDao.obtenerMetadatosCompletos();
            StringBuilder sb = new StringBuilder();
            sb.append("BASE DE DATOS: ").append(meta.getString("baseDeDatos")).append("\n========================================\n\n");
            for (Document col : meta.getList("colecciones", Document.class)) {
                sb.append("Colección: ").append(col.getString("nombreColeccion"))
                        .append(" | Documentos: ").append(col.getLong("numeroDocumentos")).append("\n  Índices:\n");
                for (Document idx : col.getList("indices", Document.class)) {
                    sb.append("    - ").append(idx.getString("nombre"))
                            .append(" | Campos: ").append(idx.getString("campos"))
                            .append(" | Único: ").append(idx.getBoolean("unico") ? "Sí" : "No").append("\n");
                }
                sb.append("\n");
            }
            txtAreaMetadatos.setText(sb.toString());
        } catch (Exception e) {
            mostrarError("Error al cargar metadatos: " + e.getMessage());
        }
    }

    private void exportarColeccion(String tipo, String formato) {
        ejecutarExportacion(tipo + "." + formato.toLowerCase(), destino -> {
            List<Document> docs = new ArrayList<>();
            String etiqueta = "item";
            switch (tipo) {
                case "animales":
                    for (Animal a : animalDao.findAll(true)) docs.add(a.toDocument());
                    etiqueta = "animal";
                    break;
                case "adoptantes":
                    for (Adoptante a : adoptanteDao.findAll(true)) docs.add(a.toDocument());
                    etiqueta = "adoptante";
                    break;
                case "adopciones":
                    for (Adopcion a : adopcionDao.findAll(true)) docs.add(a.toDocument());
                    etiqueta = "adopcion";
                    break;
            }
            if ("JSON".equals(formato)) ExportadorUtil.exportarListaAJson(docs, tipo, destino);
            else ExportadorUtil.exportarListaAXml(docs, tipo, etiqueta, destino);
        });
    }

    private void exportarMetadatos(String formato) {
        ejecutarExportacion("metadatos_refugio." + formato.toLowerCase(), destino -> {
            Document meta = metadatosDao.obtenerMetadatosCompletos();
            if ("JSON".equals(formato)) ExportadorUtil.exportarDocumentoAJson(meta, destino);
            else ExportadorUtil.exportarDocumentoAXml(meta, "metadatos", destino);
        });
    }

    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}