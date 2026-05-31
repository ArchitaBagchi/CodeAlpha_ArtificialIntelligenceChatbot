import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class ArtificialIntelligenceChatbot {
    private static final Path FAQ_FILE = Path.of("data", "task3", "chatbot_faq.txt");
    private static final Path LOG_FILE = Path.of("data", "task3", "chatbot_log.txt");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Scanner scanner = new Scanner(System.in);
    private final Map<String, String> faqResponses = new LinkedHashMap<>();
    private final List<String> chatHistory = new ArrayList<>();

    public static void main(String[] args) {
        new ArtificialIntelligenceChatbot().run();
    }

    private void run() {
        loadDefaultKnowledge();
        loadCustomKnowledge();

        System.out.println("=== Artificial Intelligence Chatbot ===");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> startChat();
                case 2 -> trainBot();
                case 3 -> showKnowledgeBase();
                case 4 -> showChatHistory();
                case 5 -> running = false;
                default -> System.out.println("Invalid choice. Try again.");
            }
        }

        saveCustomKnowledge();
        saveChatHistory();
        System.out.println("Chatbot data saved. Exiting.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Start chat");
        System.out.println("2. Train bot with a new FAQ");
        System.out.println("3. View knowledge base");
        System.out.println("4. View chat history");
        System.out.println("5. Exit");
    }

    private void startChat() {
        System.out.println();
        System.out.println("Start chatting with the bot. Type 'exit' to return to the menu.");

        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            if (input.isEmpty()) {
                System.out.println("Bot: Please type a message.");
                continue;
            }

            String response = generateResponse(input);
            String timestamp = LocalDateTime.now().format(FORMATTER);
            chatHistory.add(timestamp + " | You: " + input);
            chatHistory.add(timestamp + " | Bot: " + response);
            System.out.println("Bot: " + response);
        }
    }

    private String generateResponse(String input) {
        String normalized = normalize(input);

        for (Map.Entry<String, String> entry : faqResponses.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        if (containsAny(normalized, "hello", "hi", "hey", "namaste")) {
            return "Hello! Main aapki help ke liye yahan hoon.";
        }
        if (containsAny(normalized, "java", "oop", "object oriented")) {
            return "Java ek object-oriented language hai jisme classes, objects, inheritance aur polymorphism important concepts hote hain.";
        }
        if (containsAny(normalized, "file", "save", "storage")) {
            return "Java me file handling ke liye File, Path, Files aur streams ka use kiya jata hai.";
        }
        if (containsAny(normalized, "thread", "multithreading", "parallel")) {
            return "Multithreading Java me concurrent execution allow karta hai, jisse performance aur responsiveness improve ho sakti hai.";
        }
        if (containsAny(normalized, "spring", "hibernate", "framework")) {
            return "Spring aur Hibernate Java ecosystem ke popular frameworks hain jo backend development aur database mapping me help karte hain.";
        }
        if (containsAny(normalized, "bye", "goodbye", "thank you", "thanks")) {
            return "You're welcome! Agar aur sawal ho to pooch sakte hain.";
        }

        return "Mujhe is query ka exact answer abhi nahi pata. Aap mujhe menu ke option 2 se train kar sakte hain.";
    }

    private void trainBot() {
        System.out.println();
        System.out.println("--- Train Chatbot ---");
        String questionKeyword = normalize(readString("Enter a keyword or short FAQ trigger: "));
        String answer = readString("Enter the answer for this FAQ: ");

        faqResponses.put(questionKeyword, answer);
        System.out.println("New FAQ added successfully.");
    }

    private void showKnowledgeBase() {
        System.out.println();
        System.out.println("--- Knowledge Base ---");
        faqResponses.forEach((keyword, answer) ->
            System.out.println("Keyword: " + keyword + " | Answer: " + answer)
        );
    }

    private void showChatHistory() {
        System.out.println();
        System.out.println("--- Chat History ---");
        if (chatHistory.isEmpty()) {
            System.out.println("No chat history available.");
            return;
        }

        chatHistory.forEach(System.out::println);
    }

    private void loadDefaultKnowledge() {
        faqResponses.put("internship", "CodeAlpha internship me project completion, GitHub upload aur video explanation important deliverables hote hain.");
        faqResponses.put("certificate", "Certificate ke liye minimum required tasks complete karke proper submission form me submit karna hota hai.");
        faqResponses.put("github", "Source code ko GitHub repository me upload karke project explanation ke sath share karna chahiye.");
        faqResponses.put("submission", "Submission ke liye aapko assignment form ke through project links aur details deni hoti hain.");
    }

    private void loadCustomKnowledge() {
        try {
            Files.createDirectories(FAQ_FILE.getParent());
            if (!Files.exists(FAQ_FILE)) {
                return;
            }

            for (String line : Files.readAllLines(FAQ_FILE)) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    faqResponses.put(parts[0], parts[1]);
                }
            }
        } catch (IOException ex) {
            System.out.println("Custom FAQ load nahi ho paya. Default knowledge use ki ja rahi hai.");
        }
    }

    private void saveCustomKnowledge() {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, String> entry : faqResponses.entrySet()) {
            lines.add(entry.getKey() + "|" + entry.getValue());
        }

        try {
            Files.createDirectories(FAQ_FILE.getParent());
            Files.write(FAQ_FILE, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            System.out.println("FAQ save nahi ho paya: " + ex.getMessage());
        }
    }

    private void saveChatHistory() {
        try {
            Files.createDirectories(LOG_FILE.getParent());
            Files.write(LOG_FILE, chatHistory, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            System.out.println("Chat history save nahi ho payi: " + ex.getMessage());
        }
    }

    private boolean containsAny(String input, String... keywords) {
        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String text) {
        return text.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }

    private String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }
}
