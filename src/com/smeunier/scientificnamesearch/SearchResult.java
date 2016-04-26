package com.smeunier.scientificnamesearch;

public class SearchResult {
    private String id;
    private String name;
   
    public SearchResult(){
        super();
    }
   
    public SearchResult(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.id + ". " + this.name;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
}
