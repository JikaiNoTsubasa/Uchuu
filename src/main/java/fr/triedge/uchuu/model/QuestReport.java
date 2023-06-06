package fr.triedge.uchuu.model;

import java.util.ArrayList;

public class QuestReport {

    private ArrayList<QuestReportItem> rows = new ArrayList<>();

    public ArrayList<QuestReportItem> getRows() {
        return rows;
    }

    public void setRows(ArrayList<QuestReportItem> rows) {
        this.rows = rows;
    }

    public QuestReport add(Item item, int amount){
        getRows().add(new QuestReportItem(item, amount));
        return this;
    }
}
