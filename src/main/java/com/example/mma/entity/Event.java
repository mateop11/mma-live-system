package com.example.mma.entity;

import com.example.mma.enums.EventType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String location;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType = EventType.Professional;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rule_set_id")
    private RuleSet ruleSet;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Bout> bouts = new ArrayList<>();

    private boolean active = true;

    public Event() {}

    public Event(String name, LocalDateTime eventDate, EventType eventType) {
        this.name = name;
        this.eventDate = eventDate;
        this.eventType = eventType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public RuleSet getRuleSet() { return ruleSet; }
    public void setRuleSet(RuleSet ruleSet) { this.ruleSet = ruleSet; }

    public List<Bout> getBouts() { return bouts; }
    public void setBouts(List<Bout> bouts) { this.bouts = bouts; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

