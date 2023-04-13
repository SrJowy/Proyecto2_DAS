package com.example.proyecto1_das.data;

public class Exercise {
    private int id;
    private String name;
    private String des;
    private Integer numSeries;
    private Integer numReps;
    private Double kg;
    private String link;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDes() {
        return des;
    }

    public Integer getNumSeries() {
        return numSeries;
    }

    public Integer getNumReps() {
        return numReps;
    }

    public Double getNumKgs() {
        return kg;
    }

    public String getLink() { return link; }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setNumSeries(Integer numSeries) {
        this.numSeries = numSeries;
    }

    public void setNumReps(Integer numReps) {
        this.numReps = numReps;
    }

    public void setNumKgs(Double kgs) {
        this.kg = kgs;
    }

    public void setLink(String link) { this.link = link; }
}
