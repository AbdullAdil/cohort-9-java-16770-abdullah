package com.abdullahadil.contactmanagement.config;

import com.abdullahadil.contactmanagement.entity.Contact;
import com.abdullahadil.contactmanagement.entity.ContactEmail;
import com.abdullahadil.contactmanagement.entity.ContactLabel;
import com.abdullahadil.contactmanagement.entity.ContactPhone;
import com.abdullahadil.contactmanagement.entity.User;
import com.abdullahadil.contactmanagement.repository.ContactRepository;
import com.abdullahadil.contactmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Puts a demo account and a few contacts in the in-memory database so the app
 * has something to show on startup. Dev only - this never runs against the
 * prod profile, so no demo credentials can end up in a real database.
 */
@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private static final String DEMO_EMAIL = "demo@example.com";
    private static final String DEMO_PASSWORD = "password123";

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataSeeder(UserRepository userRepository, ContactRepository contactRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByEmail(DEMO_EMAIL)) {
            return;
        }

        User demo = userRepository.save(User.builder()
                .email(DEMO_EMAIL)
                .passwordHash(passwordEncoder.encode(DEMO_PASSWORD))
                .build());

        // Enough contacts to push the list onto a second page.
        List<String[]> people = List.of(
                new String[]{"Ayesha", "Khan", "Product Designer"},
                new String[]{"Bilal", "Ahmed", "Software Engineer"},
                new String[]{"Sana", "Malik", "Project Manager"},
                new String[]{"Hamza", "Iqbal", "QA Engineer"},
                new String[]{"Zara", "Sheikh", "Data Analyst"},
                new String[]{"Usman", "Raza", "DevOps Engineer"},
                new String[]{"Fatima", "Noor", "UX Researcher"},
                new String[]{"Omar", "Siddiqui", "Business Analyst"},
                new String[]{"Hina", "Aslam", "Scrum Master"},
                new String[]{"Danish", "Qureshi", "Backend Engineer"},
                new String[]{"Maryam", "Javed", "Frontend Engineer"},
                new String[]{"Tariq", "Mahmood", "Solutions Architect"});

        for (String[] person : people) {
            String first = person[0];
            String last = person[1];

            Contact contact = new Contact();
            contact.setOwner(demo);
            contact.setFirstName(first);
            contact.setLastName(last);
            contact.setTitle(person[2]);

            String handle = (first + "." + last).toLowerCase();
            contact.addEmail(new ContactEmail(ContactLabel.WORK, handle + "@company.com"));
            contact.addEmail(new ContactEmail(ContactLabel.PERSONAL, handle + "@example.com"));
            contact.addPhone(new ContactPhone(ContactLabel.WORK, "021-111-" + (1000 + people.indexOf(person))));
            contact.addPhone(new ContactPhone(ContactLabel.HOME, "0300-555-" + (1000 + people.indexOf(person))));

            contactRepository.save(contact);
        }

        log.info("Seeded demo account {} with {} contacts", DEMO_EMAIL, people.size());
    }
}
