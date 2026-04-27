package com.hotelnova.view;

import com.hotelnova.exception.HotelNovaException;
import com.hotelnova.model.User;
import com.hotelnova.repository.GuestRepositoryImpl;
import com.hotelnova.repository.ReservationRepositoryImpl;
import com.hotelnova.repository.RoomRepositoryImp;
import com.hotelnova.repository.UserRepositoryImpl;
import com.hotelnova.service.GuestService;
import com.hotelnova.service.ReservationService;
import com.hotelnova.service.RoomService;
import com.hotelnova.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import com.hotelnova.model.Room;
import com.hotelnova.model.RoomType;

public class MainMenu {

    private final UserService userService;
    private final RoomService roomService;
    private final GuestService guestService;
    private final ReservationService reservationService;
    private User currentUser;

    public MainMenu() {
        this.userService = new UserService(new UserRepositoryImpl());
        this.roomService = new RoomService(new RoomRepositoryImp());
        this.guestService = new GuestService(new GuestRepositoryImpl());
        this.reservationService = new ReservationService(
                new ReservationRepositoryImpl(),
                new RoomRepositoryImp(),
                new GuestRepositoryImpl());
    }

    public void start() {
        boolean authenticated = false;
        while (!authenticated) {
            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(new JLabel("Username:"));
            JTextField userField = new JTextField();
            panel.add(userField);
            panel.add(new JLabel("Password:"));
            JPasswordField passField = new JPasswordField();
            panel.add(passField);

            int result = JOptionPane.showConfirmDialog(ViewManager.parentFrame, panel, "Login HotelNova",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String user = userField.getText();
                String pass = new String(passField.getPassword());
                try {
                    currentUser = userService.authenticate(user, pass);
                    authenticated = true;
                    showMainMenu();
                } catch (Exception e) {
                    showError(e.getMessage());
                }
            } else {
                System.out.println("Login cancelado. Saliendo...");
                System.exit(0);
            }
        }
    }

    private void showMainMenu() {
        System.out.println("[LOG] GET /menu/main");

        while (true) {
            java.util.List<String> optionList = new java.util.ArrayList<>(
                    java.util.Arrays.asList("Rooms", "Guests", "Reservations", "Reports"));
            if (currentUser.getRole() == com.hotelnova.model.UserRole.ADMIN) {
                optionList.add("Users");
            }
            optionList.add("Exit");
            String[] options = optionList.toArray(new String[0]);

            String choiceStr = (String) JOptionPane.showInputDialog(ViewManager.parentFrame,
                    "Bienvenido, " + currentUser.getUsername() + "\nSeleccione una opción:",
                    "Hotel Nova - Menú Principal",
                    JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            if (choiceStr == null || choiceStr.equals("Exit")) {
                System.out.println("Saliendo del sistema...");
                System.exit(0);
            }

            switch (choiceStr) {
                case "Rooms" -> showRoomsMenu();
                case "Guests" -> showGuestsMenu();
                case "Reservations" -> showReservationsMenu();
                case "Reports" -> {
                    java.io.File defaultDir = new java.io.File(System.getProperty("user.home"), "Descargas");
                    if (!defaultDir.exists()) {
                        defaultDir = new java.io.File(System.getProperty("user.home"), "Downloads");
                    }
                    javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser(defaultDir);
                    fileChooser.setDialogTitle("Guardar Reporte CSV");
                    fileChooser.setSelectedFile(new java.io.File("reservaciones_activas.csv"));
                    
                    int userSelection = fileChooser.showSaveDialog(ViewManager.parentFrame);
                    
                    if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
                        java.io.File fileToSave = fileChooser.getSelectedFile();
                        String path = fileToSave.getAbsolutePath();
                        if (!path.toLowerCase().endsWith(".csv")) {
                            path += ".csv";
                        }
                        try {
                            reservationService.exportActiveReservations(path);
                            JOptionPane.showMessageDialog(ViewManager.parentFrame, "¡Reporte exportado exitosamente a:\n" + path,
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception e) {
                            showError("Error al exportar: " + e.getMessage());
                        }
                    }
                }
                case "Users" -> showUsersMenu();
            }
        }
    }

    private void showUsersMenu() {
        String[] options = { "Create", "List All", "Back" };
        while (true) {
            int choice = JOptionPane.showOptionDialog(ViewManager.parentFrame,
                    "Menú de Usuarios", "Users",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            try {
                switch (choice) {
                    case 0 -> {
                        String username = ViewManager.readString("Nombre de usuario:");
                        if (username == null)
                            break;

                        String password = ViewManager.readString("Contraseña:");
                        if (password == null)
                            break;

                        com.hotelnova.model.UserRole[] roles = com.hotelnova.model.UserRole.values();
                        com.hotelnova.model.UserRole role = (com.hotelnova.model.UserRole) JOptionPane.showInputDialog(
                                ViewManager.parentFrame,
                                "Seleccione el rol:", "Rol de Usuario",
                                JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);
                        if (role == null)
                            break;

                        userService.registerUser(username, password, role);
                        JOptionPane.showMessageDialog(ViewManager.parentFrame, "¡Usuario creado exitosamente!", "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    case 1 -> {
                        String table = ViewManager.formatUsersTable(userService.getAllUsers());
                        displayMonospacedText(table, "Lista de Usuarios");
                    }
                    case 2, -1 -> {
                        return;
                    }
                }
            } catch (Exception e) {
                showError("Error al procesar Usuarios: " + e.getMessage());
            }
        }
    }

    private void showRoomsMenu() {
        String[] options = { "Create", "List All", "Filter", "Edit", "Back" };
        while (true) {
            int choice = JOptionPane.showOptionDialog(ViewManager.parentFrame,
                    "Menú de Habitaciones",
                    "Rooms",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            try {
                switch (choice) {
                    case 0 -> { // Create
                        String number = JOptionPane.showInputDialog(ViewManager.parentFrame, "Número de habitación:");
                        if (number == null)
                            break;

                        RoomType[] types = RoomType.values();
                        RoomType type = (RoomType) JOptionPane.showInputDialog(ViewManager.parentFrame,
                                "Seleccione el tipo:", "Tipo de Habitación",
                                JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
                        if (type == null)
                            break;

                        BigDecimal price = ViewManager.readBigDecimal("Precio por noche:");
                        if (price == null)
                            break;

                        Room room = new Room(null, number, type, price, true);
                        roomService.registerRoom(room);
                        JOptionPane.showMessageDialog(ViewManager.parentFrame, "¡Habitación creada exitosamente!",
                                "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    case 1 -> { // List All
                        String table = ViewManager.formatRoomsTable(roomService.getAllRooms());
                        displayMonospacedText(table, "Lista de Todas las Habitaciones");
                    }
                    case 2 -> { // Filter
                        String[] filterOptions = { "Por Tipo", "Por Precio", "Cancelar" };
                        int filterChoice = JOptionPane.showOptionDialog(ViewManager.parentFrame,
                                "Filtrar habitaciones por:", "Filtro",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, filterOptions,
                                filterOptions[0]);
                        if (filterChoice == 0) {
                            RoomType[] types = RoomType.values();
                            RoomType type = (RoomType) JOptionPane.showInputDialog(ViewManager.parentFrame,
                                    "Seleccione el tipo:", "Filtro por Tipo",
                                    JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
                            if (type != null) {
                                displayMonospacedText(ViewManager.formatRoomsTable(roomService.getRoomsByType(type)),
                                        "Filtro: " + type);
                            }
                        } else if (filterChoice == 1) {
                            BigDecimal min = ViewManager.readBigDecimal("Precio mínimo:");
                            if (min == null)
                                break;
                            BigDecimal max = ViewManager.readBigDecimal("Precio máximo:");
                            if (max == null)
                                break;
                            displayMonospacedText(
                                    ViewManager.formatRoomsTable(roomService.getRoomsByPriceRange(min, max)),
                                    "Filtro Precio");
                        }
                    }
                    case 3 -> { // Edit
                        String idStr = JOptionPane.showInputDialog(ViewManager.parentFrame,
                                "Ingrese el ID de la habitación a editar:");
                        if (idStr == null)
                            break;
                        Long id = Long.parseLong(idStr);

                        RoomType[] types = RoomType.values();
                        RoomType type = (RoomType) JOptionPane.showInputDialog(ViewManager.parentFrame, "Nuevo tipo:",
                                "Editar Tipo",
                                JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
                        if (type == null)
                            break;

                        BigDecimal price = ViewManager.readBigDecimal("Nuevo precio:");
                        if (price == null)
                            break;

                        int isAvail = JOptionPane.showConfirmDialog(ViewManager.parentFrame, "¿Está disponible?",
                                "Disponibilidad", JOptionPane.YES_NO_OPTION);
                        boolean available = (isAvail == JOptionPane.YES_OPTION);

                        Room room = new Room(id, null, type, price, available);
                        roomService.updateRoom(room);
                        JOptionPane.showMessageDialog(ViewManager.parentFrame, "¡Habitación actualizada!", "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    case 4, -1 -> {
                        return;
                    }
                }
            } catch (HotelNovaException e) {
                showError("Error de validación: " + e.getMessage());
            } catch (NumberFormatException e) {
                showError("Formato numérico inválido.");
            } catch (Exception e) {
                showError("Error al procesar Habitaciones: " + e.getMessage());
            }
        }
    }

    private void showGuestsMenu() {
        String[] options = { "Create", "List", "Edit", "Back" };
        while (true) {
            int choice = JOptionPane.showOptionDialog(ViewManager.parentFrame,
                    "Menú de Huéspedes", "Guests",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            try {
                switch (choice) {
                    case 0 -> { // Create
                        String firstName = ViewManager.readString("Nombre:");
                        if (firstName == null)
                            break;
                        String lastName = ViewManager.readString("Apellido:");
                        if (lastName == null)
                            break;
                        String dni = ViewManager.readString("DNI:");
                        if (dni == null)
                            break;
                        String email = ViewManager.readString("Email:");
                        if (email == null)
                            break;

                        com.hotelnova.model.Guest guest = new com.hotelnova.model.Guest(null, firstName, lastName, dni,
                                email, true);
                        guestService.registerGuest(guest);
                        JOptionPane.showMessageDialog(ViewManager.parentFrame, "¡Huésped creado exitosamente!", "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    case 1 -> {
                        String table = ViewManager.formatGuestsTable(guestService.getAllGuests());
                        displayMonospacedText(table, "Lista de Huéspedes");
                    }
                    case 2 -> { // Edit
                        Long id = ViewManager.readLong("Ingrese el ID del Huésped a editar:");
                        if (id == null)
                            break;

                        String firstName = ViewManager.readString("Nuevo Nombre:");
                        if (firstName == null)
                            break;
                        String lastName = ViewManager.readString("Nuevo Apellido:");
                        if (lastName == null)
                            break;
                        String dni = ViewManager.readString("Nuevo DNI:");
                        if (dni == null)
                            break;
                        String email = ViewManager.readString("Nuevo Email:");
                        if (email == null)
                            break;

                        int isActiveResponse = JOptionPane.showConfirmDialog(ViewManager.parentFrame,
                                "¿El huésped está activo?", "Estado", JOptionPane.YES_NO_OPTION);
                        boolean isActive = (isActiveResponse == JOptionPane.YES_OPTION);

                        com.hotelnova.model.Guest guest = new com.hotelnova.model.Guest(id, firstName, lastName, dni,
                                email, isActive);
                        guestService.updateGuest(guest);
                        JOptionPane.showMessageDialog(ViewManager.parentFrame, "¡Huésped actualizado!", "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    case 3, -1 -> {
                        return;
                    }
                }
            } catch (Exception e) {
                showError("Error al procesar Huéspedes: " + e.getMessage());
            }
        }
    }

    private void showReservationsMenu() {
        String[] options = { "Create", "List", "Check-out", "Back" };
        while (true) {
            int choice = JOptionPane.showOptionDialog(ViewManager.parentFrame,
                    "Menú de Reservaciones",
                    "Reservations",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            try {
                switch (choice) {
                    case 0 -> { // Create (Check-in)
                        Long roomId = ViewManager.readLong("ID de la habitación:");
                        if (roomId == null)
                            break;
                        Long guestId = ViewManager.readLong("ID del huésped:");
                        if (guestId == null)
                            break;

                        java.time.LocalDate checkIn = ViewManager.readDate("Fecha de Check-in");
                        if (checkIn == null)
                            break;
                        java.time.LocalDate checkOut = ViewManager.readDate("Fecha de Check-out");
                        if (checkOut == null)
                            break;

                        com.hotelnova.model.Reservation res = new com.hotelnova.model.Reservation(null,
                                currentUser.getId(), roomId, guestId, checkIn, checkOut, null, "ACTIVE");
                        reservationService.processCheckIn(res);
                        JOptionPane.showMessageDialog(ViewManager.parentFrame, "¡Check-in realizado exitosamente!",
                                "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                    case 1 -> {
                        String table = ViewManager.formatReservationsTable(reservationService.listAllReservations());
                        displayMonospacedText(table, "Historial de Reservaciones");
                    }
                    case 2 -> { // Check-out
                        String idStr = JOptionPane.showInputDialog(ViewManager.parentFrame,
                                "Ingrese el ID de la reservación para Check-out:");
                        if (idStr == null || idStr.trim().isEmpty())
                            break;
                        Long id = Long.parseLong(idStr.trim());

                        // Obtener datos para el recibo antes de procesar
                        com.hotelnova.model.Reservation res = reservationService.listAllReservations().stream()
                                .filter(r -> r.getId().equals(id)).findFirst().orElse(null);

                        long nights = 1;
                        BigDecimal pricePerNight = BigDecimal.ZERO;

                        if (res != null) {
                            nights = Math.max(1,
                                    java.time.temporal.ChronoUnit.DAYS.between(res.getCheckIn(), res.getCheckOut()));
                            Room room = roomService.getAllRooms().stream()
                                    .filter(r -> r.getId().equals(res.getRoomId())).findFirst().orElse(null);
                            if (room != null) {
                                pricePerNight = room.getPrice();
                            }
                        }

                        BigDecimal total = reservationService.processCheckOut(id);
                        ViewManager.displayCheckOutReceipt(total, nights, pricePerNight);
                    }
                    case 3, -1 -> {
                        return;
                    }
                }
            } catch (HotelNovaException e) {
                JOptionPane.showMessageDialog(ViewManager.parentFrame, e.getMessage(), "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException e) {
                showError("Formato de ID inválido.");
            } catch (Exception e) {
                showError("Error al procesar Reservaciones: " + e.getMessage());
            }
        }
    }

    private void displayMonospacedText(String text, String title) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        JOptionPane.showMessageDialog(ViewManager.parentFrame, scrollPane, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(ViewManager.parentFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainMenu().start();
        });
    }
}
