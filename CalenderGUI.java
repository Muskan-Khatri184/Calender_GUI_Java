import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class CalenderGUI implements Runnable {
    private final ArrayList<Event> events; // ArrayList to store events
    private final DefaultListModel<String> eventListModel;
    private final JList<String> eventList;

    private static final String EVENTS_FILE = "D:\\events.txt";

    private static class Event {
        private final LocalDate date;
        private final String eventName;
        public Event(LocalDate date, String eventName) {
            this.date = date;
            this.eventName = eventName;
        }
        public LocalDate getDate() {
            return date;
        }
        public String getEventName() {
            return eventName;
        }
        @Override
        public String toString() {
            return date + " - " + eventName;
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new CalenderGUI());
    }
    private static final String[] DAY_NAMES = {"Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday"};
    private static final String[] MONTH_NAMES = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    private JComboBox<String> monthComboBox;
    private JPanel[][] dayPanel;
    private JLabel titleLabel;
    private JTextField dayField;
    private JTextField yearField;

    private LocalDate calendarDate;

    public CalenderGUI() {
        this.events = new ArrayList<>();
        loadEvents(); // Load events from file upon initialization
        this.calendarDate = LocalDate.now();
        eventListModel = new DefaultListModel<>();
        eventList = new JList<>(eventListModel);
    }

    @Override
    public void run() {
        JFrame frame = new JFrame("Calendar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(createTopPanel(), BorderLayout.NORTH);
        frame.add(createCalendarPanel(), BorderLayout.CENTER);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(230, 200, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel labelsPanel = new JPanel(new BorderLayout()); // Panel to hold the labels
        labelsPanel.add(createWeekdayLabels(), BorderLayout.NORTH); // Add weekday labels to the top
        labelsPanel.add(createDayLabels(), BorderLayout.CENTER); // Add day labels below weekday labels

        panel.add(labelsPanel, BorderLayout.CENTER); // Add labels panel to the center
        panel.add(createEventList(), BorderLayout.WEST); // Add event list to the west side
        labelsPanel.setBackground(new Color(230, 200, 250));

        updateDayLabels();

        return panel;
    }

    private JScrollPane createEventList() {
        JScrollPane scrollPane = new JScrollPane(eventList);
        // Populate the event list with existing events
        for (Event event : events) {
            eventListModel.addElement(event.toString());
        }
        return scrollPane;
    }
    // Method to remove an event
    private void removeEvent(Event eventToRemove) {
        // Remove the event from your events collection
        events.remove(eventToRemove);

        // Update the DefaultListModel to reflect the removal
        eventListModel.removeElement(eventToRemove.toString());
    }
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createDatePanel(), BorderLayout.NORTH);
        panel.add(createTitlePanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDatePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        Font font = panel.getFont().deriveFont(12f);
        panel.setBackground(new Color(230, 200, 250));
        JButton previousYearButton = new JButton("PREVIOUS YEAR");
        previousYearButton.setBackground(new Color(50, 43, 61));
        previousYearButton.setForeground(Color.WHITE);
        previousYearButton.addActionListener(new PreviousYearButtonListener());
        panel.add(previousYearButton);

        JButton previousMonthButton = new JButton("PREVIOUS MONTH");
        previousMonthButton.setBackground(new Color(50, 43, 61));
        previousMonthButton.setForeground(Color.WHITE);
        previousMonthButton.addActionListener(new PreviousMonthButtonListener());
        panel.add(previousMonthButton);

        monthComboBox = new JComboBox<>(MONTH_NAMES);
        monthComboBox.setEditable(false);
        monthComboBox.setFont(font);
        monthComboBox.setSelectedIndex(calendarDate.getMonth().ordinal());
        monthComboBox.setBackground(new Color(50, 43, 61));
        monthComboBox.setForeground(Color.WHITE);
        monthComboBox.addActionListener(e -> updateDayLabels());
        panel.add(monthComboBox);

        dayField = new JTextField(2);
        dayField.setBackground(new Color(50, 43, 61));
        dayField.setForeground(Color.WHITE);
        dayField.setFont(font);
        dayField.setText(Integer.toString(calendarDate.getDayOfMonth()));
        panel.add(dayField);

        yearField = new JTextField(4);
        yearField.setBackground(new Color(50, 43, 61));
        yearField.setForeground(Color.WHITE);
        yearField.setFont(font);
        yearField.setText(Integer.toString(calendarDate.getYear()));
        panel.add(yearField);

        JButton button = new JButton("ADD EVENTS");
        button.setBackground(new Color(50, 43, 61));
        button.setForeground(Color.WHITE);
        button.addActionListener(new CreateCalendarButtonListener());
        button.setFont(font);
        panel.add(button);

        JButton nextMonthButton = new JButton("NEXT MONTH");
        nextMonthButton.setBackground(new Color(50, 43, 61));
        nextMonthButton.setForeground(Color.WHITE);
        nextMonthButton.addActionListener(new NextMonthButtonListener());
        panel.add(nextMonthButton);

        JButton nextYearButton = new JButton("NEXT YEAR");
        nextYearButton.setBackground(new Color(50, 43, 61));
        nextYearButton.setForeground(Color.WHITE);
        nextYearButton.addActionListener(new NextYearButtonListener());
        panel.add(nextYearButton);

        return panel;
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(50, 43, 61));
        Font font = panel.getFont().deriveFont(40f).deriveFont(Font.ITALIC);
        titleLabel = new JLabel(" ");
        titleLabel.setFont(font);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        return panel;
    }

    private JPanel createWeekdayLabels() {
        JPanel panel = new JPanel(new GridLayout(0, DAY_NAMES.length, 5, 5));
        panel.setBackground(new Color(50, 43, 61));
        Font dayNamesFont = panel.getFont().deriveFont(16f).deriveFont(Font.BOLD);
        for (String dayName : DAY_NAMES) {
            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setBackground(new Color(230, 200, 250));
            dayPanel.setPreferredSize(new Dimension(80, 40));

            JLabel label = new JLabel(dayName);
            label.setFont(dayNamesFont);
            label.setHorizontalAlignment(JLabel.CENTER);
            dayPanel.add(label, BorderLayout.CENTER);

            panel.add(dayPanel);
        }

        return panel;
    }
    private JPanel createDayLabels() {
        JPanel panel = new JPanel(new GridLayout(0, DAY_NAMES.length, 2, 2));
        dayPanel = new JPanel[6][DAY_NAMES.length];
        panel.setBackground(new Color(50, 43, 61));
        Font dayFont = panel.getFont().deriveFont(20f).deriveFont(Font.BOLD);
        Font eventFont = panel.getFont().deriveFont(12f); // Adjust the font size here

        for (int j = 0; j < dayPanel.length; j++) {
            for (int i = 0; i < dayPanel[j].length; i++) {
                dayPanel[j][i] = new JPanel(new BorderLayout());
                dayPanel[j][i].setBorder(BorderFactory.createLineBorder(new Color(50, 43, 61), 1));
                dayPanel[j][i].setPreferredSize(new Dimension(80, 80));
                dayPanel[j][i].setBackground(new Color(230,200,250));
                JLabel dayLabel = new JLabel(" ");
                dayLabel.setFont(dayFont);
                dayLabel.setHorizontalAlignment(JLabel.CENTER);
                JLabel eventLabel = new JLabel(" ");
                eventLabel.setFont(eventFont);
                eventLabel.setHorizontalAlignment(JLabel.CENTER);
                JPanel container = new JPanel(new GridLayout(2, 1));
                container.setBackground(new Color(230,200,250));
                container.add(dayLabel);
                container.add(eventLabel);

                dayPanel[j][i].add(container, BorderLayout.CENTER);

                // Enable mouse events
                dayPanel[j][i].setEnabled(true);

                // Add mouse listener to handle event removal directly to the JLabel
                dayPanel[j][i].addMouseListener(new EventRemovalMouseListener(dayLabel, eventLabel));
                panel.add(dayPanel[j][i]);}}
        return panel;}

    public void updateDayLabels() {
        int month = monthComboBox.getSelectedIndex();
        int year = Integer.parseInt(yearField.getText());
        calendarDate = LocalDate.of(year, month + 1, 1);

        titleLabel.setText(MONTH_NAMES[month] + " " + year);

        // Get the first day of the month and the number of days in the month
        int firstDayOfMonth = calendarDate.getDayOfWeek().getValue() % 7;
        int daysInMonth = calendarDate.lengthOfMonth();

        // Clear all day labels
        for (JPanel[] panels : dayPanel) {
            for (JPanel panel : panels) {
                JLabel dayLabel = (JLabel) ((JPanel) panel.getComponent(0)).getComponent(0);
                dayLabel.setText(" ");
                JLabel eventLabel = (JLabel) ((JPanel) panel.getComponent(0)).getComponent(1);
                eventLabel.setText(" ");
            }
        }

        int dayCounter = 1;
        for (int i = firstDayOfMonth; i < dayPanel[0].length; i++) {
            updateDayLabel(0, i, dayCounter++, month, year);
        }

        for (int j = 1; j < dayPanel.length; j++) {
            for (int i = 0; i < dayPanel[j].length; i++) {
                if (dayCounter > daysInMonth) {
                    return;
                }
                updateDayLabel(j, i, dayCounter++, month, year);
            }
        }
    }
    private void updateDayLabel(int row, int col, int day, int month, int year) {
        JPanel panel = dayPanel[row][col];
        JLabel dayLabel = (JLabel) ((JPanel) panel.getComponent(0)).getComponent(0);
        JLabel eventLabel = (JLabel) ((JPanel) panel.getComponent(0)).getComponent(1);
        dayLabel.setText(String.valueOf(day));
        LocalDate currentDate = LocalDate.of(year, month + 1, day);
        if (currentDate.equals(LocalDate.now())) {
            panel.setBackground(Color.GRAY); // Set background color for the current date
        }
        for (Event event : events) {
            if (event.getDate().equals(currentDate)) {
                eventLabel.setText(event.getEventName());
                break;
            }
        }
    }

    private void loadEvents() {
        File eventsFile = new File(EVENTS_FILE);

        if (eventsFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(eventsFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    LocalDate date = LocalDate.parse(parts[0]);
                    String eventName = parts[1];
                    events.add(new Event(date, eventName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveEvents() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EVENTS_FILE))) {
            for (Event event : events) {
                bw.write(event.getDate() + "," + event.getEventName());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class PreviousYearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int year = Integer.parseInt(yearField.getText()) - 1;
            yearField.setText(Integer.toString(year));
            updateDayLabels();
        }}
    private class PreviousMonthButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int month = monthComboBox.getSelectedIndex() - 1;
            if (month < 0) {
                month = MONTH_NAMES.length - 1;
                int year = Integer.parseInt(yearField.getText()) - 1;
                yearField.setText(Integer.toString(year));
            }
            monthComboBox.setSelectedIndex(month);
            updateDayLabels();
        }}
    private class NextYearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int year = Integer.parseInt(yearField.getText()) + 1;
            yearField.setText(Integer.toString(year));
            updateDayLabels();}}

    private class NextMonthButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int month = monthComboBox.getSelectedIndex() + 1;
            if (month >= MONTH_NAMES.length) {
                month = 0;
                int year = Integer.parseInt(yearField.getText()) + 1;
                yearField.setText(Integer.toString(year));
            }
            monthComboBox.setSelectedIndex(month);
            updateDayLabels();
        }
    }
    private class CreateCalendarButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int year = Integer.parseInt(yearField.getText());
            int month = monthComboBox.getSelectedIndex() + 1;
            int day = Integer.parseInt(dayField.getText());
            calendarDate = LocalDate.of(year, month, day);

            String eventName = JOptionPane.showInputDialog("Enter event name:");
            if (eventName != null && !eventName.isEmpty()) {
                Event event = new Event(calendarDate, eventName);
                events.add(event);

                // Update the DefaultListModel
                eventListModel.addElement(event.toString());

                saveEvents();
                updateDayLabels();
            }
        }
    }

    private class EventRemovalMouseListener extends MouseAdapter {
        private final JLabel dayLabel;
        private final JLabel eventLabel;

        public EventRemovalMouseListener(JLabel dayLabel, JLabel eventLabel) {
            this.dayLabel = dayLabel;
            this.eventLabel = eventLabel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            String dayText = dayLabel.getText();

            if (!dayText.equals(" ")) {
                int year = Integer.parseInt(yearField.getText());
                int month = monthComboBox.getSelectedIndex() + 1;
                int day = Integer.parseInt(dayText);
                LocalDate clickedDate = LocalDate.of(year, month, day);

                // Get the list of events for the selected date
                ArrayList<Event> eventsForDate = new ArrayList<>();
                for (Event event : events) {
                    if (event.getDate().equals(clickedDate)) {
                        eventsForDate.add(event);
                    }
                }

                if (!eventsForDate.isEmpty()) {
                    // Create an array of event names for the dialog
                    String[] eventNames = new String[eventsForDate.size()];
                    for (int i = 0; i < eventsForDate.size(); i++) {
                        eventNames[i] = eventsForDate.get(i).getEventName();
                    }

                    // Show a dialog to select the event to remove
                    String eventNameToRemove = (String) JOptionPane.showInputDialog(
                            null,
                            "Select event to remove:",
                            "Remove Event",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            eventNames,
                            eventNames[0]);

                    if (eventNameToRemove != null) {
                        // Find the selected event and remove it
                        Event eventToRemove = null;
                        for (Event event : eventsForDate) {
                            if (event.getEventName().equals(eventNameToRemove)) {
                                eventToRemove = event;
                                break;
                            }
                        }

                        if (eventToRemove != null) {
                            removeEvent(eventToRemove);
                            saveEvents();
                            updateDayLabels();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No events found for this date.");
                }
            }
        }
    }

}