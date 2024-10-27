/*
 * The TableView class is a component used to manage table names in a PrimeFaces application.
 * This class allows for adding, removing, and editing table names. It starts with three predefined table names
 * and can be updated through user interactions. Additionally, it depends on the TestView component
 * and contains methods for loading the corresponding data for the tables.
 */

package org.primefaces.test;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Named
@SessionScoped
public class TableView implements Serializable {

    private List<String> tables;
    private String newTableName;
    private String editedTableName;
    private int editedTableIndex = -1;
    @Inject
    private TestView testView;



    @PostConstruct
    public void init() {
        tables = new ArrayList<>();
        tables.add("Ankara");
        tables.add("İstanbul");
        tables.add("İzmir");
    }

    public void addTable() {
        if (newTableName != null && !newTableName.trim().isEmpty()) {
            tables.add(newTableName.trim());
            newTableName = null;
        }
    }

    public void removeTable(String table) {
        if (table != null && tables.contains(table)) {
            tables.remove(table);
            System.out.println("Updated list: " + tables);
        }
    }

    public void updateTable() {
        testView.loadInitialData();
        Map<String, List<TestObject>> tableData = testView.getTableData();
        Map<String, List<TestObject>> copyOfTableData = new HashMap<>(tableData);
        for (Map.Entry<String, List<TestObject>> entry : copyOfTableData.entrySet()) {
            if (entry.getKey() != null && editedTableName.contains(entry.getKey())) {
                    testView.loadDataAgain(editedTableName);
            }else{
                continue;
            }
        }
        if (editedTableIndex >= 0 && editedTableIndex < tables.size()
                && editedTableName != null && !editedTableName.trim().isEmpty()) {
            tables.set(editedTableIndex, editedTableName.trim());
            editedTableName = null;
            editedTableIndex = -1;
        }
    }

    public void prepareEditTable(String tableName, Integer index) {
        if (index != null && index >= 0 && index < tables.size()) {
            this.editedTableName = tableName;
            this.editedTableIndex = index;
        }
    }


    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public String getNewTableName() {
        return newTableName;
    }

    public void setNewTableName(String newTableName) {
        this.newTableName = newTableName;
    }

    public String getEditedTableName() {
        return editedTableName;
    }

    public void setEditedTableName(String editedTableName) {
        this.editedTableName = editedTableName;
    }

    public int getEditedTableIndex() {
        return editedTableIndex;
    }

    public void setEditedTableIndex(int editedTableIndex) {
        this.editedTableIndex = editedTableIndex;
    }
}