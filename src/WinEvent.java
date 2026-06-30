import javax.swing.*;
import java.awt.*;

public class WinEvent extends JFrame {
    ImageIcon icon;
    JLabel l_North;
    PanelSearchAll scrollPane;
    PanelInsert panelInsert;
    PanelDelete panelDelete;
    PanelUpdate panelUpdate;
    // 新增收银面板变量
    PanelCashier panelCashier;
    JTabbedPane tabbedPane;

    public WinEvent(String s) {
        super(s);
        setBounds(800, 80, 550, 550);
        init();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        validate();
    }

    void init() {
        icon = new ImageIcon("icon/logo2.png");
        l_North = new JLabel(icon);

        tabbedPane = new JTabbedPane();
        // 1. 先创建查询面板（所有子面板共用它实现刷新）
        scrollPane = new PanelSearchAll();
        // 2. 仅实例化一次插入、删除、修改（只一次！不会重复）
        panelInsert = new PanelInsert(scrollPane);
        panelDelete = new PanelDelete(scrollPane);
        panelUpdate = new PanelUpdate(scrollPane);
        // 3. 实例化收银面板
        panelCashier = new PanelCashier(scrollPane);

        // 4. 添加选项卡：每个功能只 add 一次，顺序如下
        tabbedPane.add("全部查询", scrollPane);
        tabbedPane.add("插入商品", panelInsert);
        tabbedPane.add("删除商品", panelDelete);
        tabbedPane.add("修改商品", panelUpdate);
        tabbedPane.add("收银结算", panelCashier);

        add(l_North, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
}