import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PanelSearchAll extends JPanel {
    String[] columnNames;
    String[][] rowData;
    JTable table;
    JScrollPane scrollPane;
    DefaultTableModel model;

    public PanelSearchAll() {
        setTable();
        model = new DefaultTableModel(rowData, columnNames);
        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        add(scrollPane);
        setBackground(Color.GREEN);
    }

    void setTable() {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = GetDBConnection.connectDB();
            if(con == null){
                JOptionPane.showMessageDialog(this,"数据库连接失败！请先启动MySQL服务后重新打开程序");
                return;
            }
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery("SELECT * FROM goods");

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }

            rs.last();
            int rowCount = rs.getRow();
            rs.first();
            rowData = new String[rowCount][columnCount];
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    rowData[i][j] = rs.getString(j + 1);
                }
                rs.next();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,"查询数据失败："+e.getMessage());
            e.printStackTrace();
        } finally {
            GetDBConnection.close(con, stmt, rs);
        }
    }

    // 对外提供刷新表格方法
    public void refreshTable(){
        setTable();
        model.setDataVector(rowData, columnNames);
        table.setModel(model);
    }
}