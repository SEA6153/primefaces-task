/*
 * This Java class represents a managed bean in a JavaServer Faces (JSF) application that handles various operations for a music-related table.
 * It uses Jakarta EE annotations to define its scope and lifecycle, specifically as a session-scoped bean.
 * The class is designed to manage a collection of TestObject instances representing musical items with properties such as first name, last name, song title, artist, and release year.
 *
 * The class includes the following key functionalities:
 *
 * - **Attributes**: It maintains state with several properties, including string values, integers, BigDecimal, LocalDateTime, and a map of table data that associates table names with lists of TestObject instances.
 * - **Initialization**: The @PostConstruct method initializes the table data with predefined entries and checks for any selected table from request parameters.
 * - **Data Management**: It provides methods to load initial data, select tables, update current table data, and validate new items before adding them.
 * - **CRUD Operations**: The class implements methods for creating new items, updating existing items, deleting items, and editing items.
 * - **Validation**: Several helper methods ensure the validity of data before processing, such as checking for null or empty fields.
 * - **User Feedback**: It uses FacesContext to add messages for user feedback, indicating success or failure of operations.
 *
 * The TestObject class is assumed to be a simple Java bean with appropriate fields and methods.
 */
package org.primefaces.test;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Data;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Named
@SessionScoped
public class TestView implements Serializable {

    private String string;
    private Integer integer;
    private BigDecimal decimal;
    private LocalDateTime localDateTime;
    private Map<String, List<TestObject>> tableData;

    private String selectedTable;
    private List<TestObject> currentTableData;
    private String newTableName;
    private boolean isEditing;
    private int editingIndex;
    private TestObject editedItem;
    private TestObject newItem;
    private int editedItemIndex = -1;

    @PostConstruct
    public void init() {
        tableData = new HashMap<>();
        newItem = new TestObject();
        loadInitialData();

        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        selectedTable = params.get("selectedTable");

        if (selectedTable != null && !selectedTable.isEmpty() && tableData.containsKey(selectedTable)) {
            currentTableData = tableData.get(selectedTable);
            System.out.println("Loaded data for table: " + selectedTable);
        } else {
            currentTableData = new ArrayList<>();
            System.out.println("No selected table found or valid, initializing with empty data.");
        }
    }
    public void loadInitialData() {


            tableData.put("Ankara", new ArrayList<>(List.of(
                    new TestObject("Samet Ege", "AŞIK", "Master of Puppets", "Metallica", 1986),
                    new TestObject("Ahmet", "YILMAZ", "Soldier of Fortune", "Deep Purple", 1974)
            )));

            tableData.put("İstanbul", new ArrayList<>(List.of(
                    new TestObject("Ali", "VELİ", "Shape of You", "Ed Sheeran", 2017),
                    new TestObject("Zeynep", "ÇINAR", "Blinding Lights", "The Weeknd", 2019)
            )));

            tableData.put("İzmir", new ArrayList<>(List.of(
                    new TestObject("Elif", "KAYA", "Hallelujah", "Leonard Cohen", 1984),
                    new TestObject("Burak", "ÖZTÜRK", "Rolling in the Deep", "Adele", 2010)
            )));

            System.out.println("Data loaded for tables: " + tableData);


    }

    public void loadDataAgain(String tableName) {
        if (tableName != null) {
            boolean found = false;

            for (Map.Entry<String, List<TestObject>> entry : tableData.entrySet()) {

                if (entry.getKey() != null && tableName.contains(entry.getKey())) {
                    tableData.put(tableName, new ArrayList<>(entry.getValue()));
                    found = true;
                    break;
                }
            }
            if (!found) {
                tableData.put(tableName, null);
            }
        }
    }



    public List<String> getTables() {
        return new ArrayList<>(tableData.keySet());
    }


    public boolean isSelectedTableExists() {
        return tableData.keySet().stream()
                .anyMatch(key -> key.equalsIgnoreCase(selectedTable));
    }
    public void selectTable(String tableName) {
        if (isSelectedTableExists()) {
            this.selectedTable = tableName;
            this.currentTableData = tableData.entrySet().stream()
                    .filter(entry -> entry.getKey().equalsIgnoreCase(selectedTable))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(new ArrayList<>());
            updateCurrentTableData();
        } else {
            this.currentTableData = new ArrayList<>();
        }
    }

    public void updateCurrentTableData() {
        if (selectedTable != null && isSelectedTableExists()) {
            currentTableData = tableData.entrySet().stream()
                    .filter(entry -> entry.getKey().equalsIgnoreCase(selectedTable))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(new ArrayList<>());
        } else {
            currentTableData = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("No data for selected table."));
        }
    }
    public void saveEditItem() {
        if (editedItem != null && editingIndex >= 0 && editingIndex < currentTableData.size()) {
            TestObject itemToUpdate = currentTableData.get(editingIndex);

            itemToUpdate.setFirstName(editedItem.getFirstName());
            itemToUpdate.setLastName(editedItem.getLastName());
            itemToUpdate.setSong(editedItem.getSong());
            itemToUpdate.setArtist(editedItem.getArtist());
            itemToUpdate.setReleased(editedItem.getReleased());

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Düzenleme Başarılı", "Öğe başarıyla güncellendi!"));

            isEditing = false;
            editedItem = null;
            editingIndex = -1;

            PrimeFaces.current().ajax().update("form1:dynamicTable"); // Tabloyu güncelle
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hata", "Düzenleme işlemi başarısız!"));
        }
    }

    public void saveNewItem() {
        if (newItem != null && isNewItemValid(newItem)) {
            currentTableData.add(newItem);
            newItem = new TestObject();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Yeni item başarıyla eklendi!"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hata!", "Veri eksik"));
        }
    }
    private boolean isNewItemValid(TestObject item) {
        return item.getFirstName() != null && !item.getFirstName().isEmpty()
                && item.getLastName() != null && !item.getLastName().isEmpty()
                && item.getSong() != null && !item.getSong().isEmpty()
                && item.getArtist() != null && !item.getArtist().isEmpty()
                && item.getReleased() != null;
    }

    public void deleteItem(TestObject item) {
        getCurrentTableData().remove(item);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Item başarıyla silindi!"));
    }

    public void editItem(TestObject item) {
        if (item != null && currentTableData != null) {
            editedItem = new TestObject(item.getFirstName(), item.getLastName(), item.getSong(), item.getArtist(), item.getReleased());
            editingIndex = currentTableData.indexOf(item);
            isEditing = true;
        }
    }

    public TestObject getEditedItem() {
        if (editedItem == null) {
            editedItem = new TestObject();
        }
        return editedItem;
    }

    public void addNewTable() {
        if (newTableName != null && !newTableName.trim().isEmpty() && !tableData.containsKey(newTableName)) {
            tableData.put(newTableName, new ArrayList<>());
            selectedTable = newTableName;
            updateCurrentTableData();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New Table Added", "Table " + newTableName + " added successfully"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Table Name", "Please enter a unique table name"));
        }
        newTableName = "";
    }
    public List<TestObject> getCurrentTableData() {
        return tableData.computeIfAbsent(selectedTable, k -> new ArrayList<>());
    }
    public void prepareEdit(TestObject item) {
        if (item != null) {
            editedItem = new TestObject(item.getFirstName(), item.getLastName(), item.getSong(), item.getArtist(), item.getReleased());
            editingIndex = currentTableData.indexOf(item);
            isEditing = true;
        }
    }
    public void updateItem() {
        if (editedItem == null || editingIndex < 0 || editingIndex >= currentTableData.size()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Geçersiz düzenleme işlemi", "Lütfen geçerli bir öğe seçin."));
            return;
        }

        if (!editedItem.isValid()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Geçersiz veri", "Lütfen tüm alanları doldurun."));
            return;
        }

        currentTableData.set(editingIndex, editedItem);

        updateCurrentTableData();

        editedItem = new TestObject();
        editingIndex = -1;
    }
    public boolean isEditedItemValid() {
        return editedItem != null &&
                editedItem.getFirstName() != null && !editedItem.getFirstName().isEmpty() &&
                editedItem.getLastName() != null && !editedItem.getLastName().isEmpty() &&
                editedItem.getSong() != null && !editedItem.getSong().isEmpty() &&
                editedItem.getArtist() != null && !editedItem.getArtist().isEmpty() &&
                editedItem.getReleased() != null;
    }
    public void setEditedItem(TestObject editedItem) {
        this.editedItem = editedItem;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public BigDecimal getDecimal() {
        return decimal;
    }

    public void setDecimal(BigDecimal decimal) {
        this.decimal = decimal;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public Map<String, List<TestObject>> getTableData() {
        return tableData;
    }

    public void setTableData(Map<String, List<TestObject>> tableData) {
        this.tableData = tableData;
    }

    public TestObject getNewItem() {
        return newItem;
    }

    public void setNewItem(TestObject newItem) {
        this.newItem = newItem;
    }

    public String getSelectedTable() {
        return selectedTable;
    }

    public void setSelectedTable(String selectedTable) {
        this.selectedTable = selectedTable;
    }

    public void setCurrentTableData(List<TestObject> currentTableData) {
        this.currentTableData = currentTableData;
    }

    public String getNewTableName() {
        return newTableName;
    }

    public void setNewTableName(String newTableName) {
        this.newTableName = newTableName;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    public int getEditingIndex() {
        return editingIndex;
    }

    public void setEditingIndex(int editingIndex) {
        this.editingIndex = editingIndex;
    }

    public int getEditedItemIndex() {
        return editedItemIndex;
    }

    public void setEditedItemIndex(int editedItemIndex) {
        this.editedItemIndex = editedItemIndex;
    }
}

