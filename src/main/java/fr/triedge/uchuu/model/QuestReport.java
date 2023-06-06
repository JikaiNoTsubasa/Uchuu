package fr.triedge.uchuu.model;

import java.util.ArrayList;

public class QuestReport {

    private int xp;
    private QuestStatus status;
    private ArrayList<QuestReportItem> rows = new ArrayList<>();

    public QuestStatus getStatus() {
        return status;
    }

    public void setStatus(QuestStatus status) {
        this.status = status;
    }

    public ArrayList<QuestReportItem> getRows() {
        return rows;
    }

    public void setRows(ArrayList<QuestReportItem> rows) {
        this.rows = rows;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public QuestReport add(Item item, int amount){
        getRows().add(new QuestReportItem(item, amount));
        return this;
    }
}
