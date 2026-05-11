package br.com.outsera.infrastructure.csv;

public enum HeadersCsv {

    YEAR("year"),
    TITLE("title"),
    STUDIOS("studios"),
    PRODUCERS("producers"),
    WINNER("winner");

    private final String header;

    HeadersCsv(String header) {
        this.header = header;
     }

     public String getHeader() {
         return header;
     }
}
