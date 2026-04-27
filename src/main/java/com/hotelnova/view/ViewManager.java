package com.hotelnova.view;

import com.hotelnova.model.Guest;
import com.hotelnova.model.Reservation;
import com.hotelnova.model.Room;

import com.hotelnova.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JOptionPane;

public class ViewManager {
    public static final javax.swing.JFrame parentFrame = new javax.swing.JFrame();
    static {
        parentFrame.setAlwaysOnTop(true);
    }


    public static String readString(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(parentFrame, message, "Entrada de Datos", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return null;
            if (!input.trim().isEmpty()) return input.trim();
            JOptionPane.showMessageDialog(parentFrame, "El campo no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static Long readLong(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(parentFrame, message, "Entrada Numérica", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return null;
            try {
                return Long.parseLong(input.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentFrame, "Por favor, ingrese un número entero válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static LocalDate readDate(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(parentFrame, message + " (YYYY-MM-DD):", "Entrada de Fecha", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return null;
            try {
                return LocalDate.parse(input.trim());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(parentFrame, "Por favor, ingrese una fecha válida en formato YYYY-MM-DD.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static BigDecimal readBigDecimal(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(parentFrame, message, "Entrada de Precio", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return null; // El usuario canceló
            try {
                return new BigDecimal(input.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentFrame, "Por favor, ingrese un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void displayCheckOutReceipt(BigDecimal total, long nights, BigDecimal pricePerNight) {
        String receipt = String.format(
                "Stay Duration: %d nights.\nPrice per night: $%.2f.\nTotal with Taxes (19%% VAT): $%.2f.",
                nights, pricePerNight, total);
        JOptionPane.showMessageDialog(parentFrame, receipt, "Check-out Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    public static String formatUsersTable(List<User> users) {
        if (users == null || users.isEmpty()) {
            return "No hay usuarios para mostrar.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-5s | %-20s | %-15s |\n", "ID", "USERNAME", "ROLE"));
        sb.append("-".repeat(49)).append("\n");
        
        for (User u : users) {
            String idStr = u.getId() != null ? String.valueOf(u.getId()) : "N/A";
            sb.append(String.format("| %-5s | %-20s | %-15s |\n",
                    idStr, u.getUsername(), u.getRole().name()));
        }
        return sb.toString();
    }

    public static String formatRoomsTable(List<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            return "No hay habitaciones para mostrar.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-5s | %-10s | %-15s | %-10s | %-13s |\n", "ID", "NUMBER", "TYPE", "PRICE", "STATUS"));
        sb.append("-".repeat(68)).append("\n");
        
        for (Room r : rooms) {
            String idStr = r.getId() != null ? String.valueOf(r.getId()) : "N/A";
            String typeStr = r.getType() != null ? r.getType().name() : "N/A";
            String priceStr = r.getPrice() != null ? r.getPrice().toString() : "N/A";
            
            sb.append(String.format("| %-5s | %-10s | %-15s | %-10s | %-13s |\n",
                    idStr, r.getNumber(), typeStr, priceStr, r.isAvailable() ? "[DISPONIBLE]" : "[OCUPADA]"));
        }
        return sb.toString();
    }

    public static String formatGuestsTable(List<Guest> guests) {
        if (guests == null || guests.isEmpty()) {
            return "No hay huéspedes para mostrar.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-5s | %-15s | %-15s | %-12s | %-25s | %-10s |\n", "ID", "FIRST NAME", "LAST NAME", "DNI", "EMAIL", "STATUS"));
        sb.append("-".repeat(97)).append("\n");
        
        for (Guest g : guests) {
            String idStr = g.getId() != null ? String.valueOf(g.getId()) : "N/A";
            sb.append(String.format("| %-5s | %-15s | %-15s | %-12s | %-25s | %-10s |\n",
                    idStr, g.getFirstName(), g.getLastName(), g.getDni(), g.getEmail(), g.isActive() ? "[ACTIVO]" : "[INACTIVO]"));
        }
        return sb.toString();
    }

    public static String formatReservationsTable(List<Reservation> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            return "No hay reservaciones para mostrar.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-5s | %-8s | %-9s | %-10s | %-10s | %-10s | %-10s |\n", "ID", "ROOM ID", "GUEST ID", "CHECK-IN", "CHECK-OUT", "PRICE", "STATUS"));
        sb.append("-".repeat(82)).append("\n");
        
        for (Reservation r : reservations) {
            String idStr = r.getId() != null ? String.valueOf(r.getId()) : "N/A";
            String roomStr = r.getRoomId() != null ? String.valueOf(r.getRoomId()) : "N/A";
            String guestStr = r.getGuestId() != null ? String.valueOf(r.getGuestId()) : "N/A";
            String checkInStr = r.getCheckIn() != null ? r.getCheckIn().toString() : "N/A";
            String checkOutStr = r.getCheckOut() != null ? r.getCheckOut().toString() : "N/A";
            String priceStr = r.getTotalPrice() != null ? r.getTotalPrice().toString() : "N/A";
            
            sb.append(String.format("| %-5s | %-8s | %-9s | %-10s | %-10s | %-10s | %-10s |\n",
                    idStr, roomStr, guestStr, checkInStr, checkOutStr, priceStr, r.getStatus()));
        }
        return sb.toString();
    }
}
