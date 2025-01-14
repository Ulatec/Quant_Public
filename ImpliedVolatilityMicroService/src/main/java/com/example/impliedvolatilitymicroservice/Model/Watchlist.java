package com.example.impliedvolatilitymicroservice.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Watchlist {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @ElementCollection
    public List<String> longTickers;

    @ElementCollection
    public List<String> shortTickers;

    public int size;

    public String name;


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLongTickers() {
        return longTickers;
    }

    public void setLongTickers(List<String> longTickers) {
        this.longTickers = longTickers;
    }

    public List<String> getShortTickers() {
        return shortTickers;
    }

    public void setShortTickers(List<String> shortTickers) {
        this.shortTickers = shortTickers;
    }
}
