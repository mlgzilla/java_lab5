package com.company;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Form extends JDialog {

    private JPanel rootPanel;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JButton addButton;
    private JButton delButton;
    private JButton calkButton;
    private JButton clearButton;
    private JButton fillButton;

    private JTextField input1;
    private JTextField input2;
    private JTextField input3;
    private JTable table1;
    private JButton saveBButton;
    private JButton saveTButton;
    private JButton loadTButton;
    private JButton loadBButton;

    class RecIntegral implements Serializable{
        String Upper, Lower, Step, Result;

        String getUpper() {
            return Upper;
        }

        String getLower() {
            return Lower;
        }

        String getStep() {
            return Step;
        }

        String getResult() {
            return Result;
        }

        void setUpper(String Temp) {
            this.Upper = Temp;
        }

        void setLower(String Temp) {
            this.Lower = Temp;
        }

        void setStep(String Temp) {
            this.Step = Temp;
        }

        void setResult(String Temp) {
            this.Result = Temp;
        }

        void setAll(String limUp, String limDown, String step, String result){
            this.setUpper(limUp);
            this.setLower(limDown);
            this.setStep(step);
            this.setResult(result);
        }
    }

    static class MyException extends Exception {
        String msg;

        MyException(String code) {
            msg = code;
        }
    }

    public double Calk(String Upper, String Lower, String Step) {
        double sum = 0;
        double limUp = Double.parseDouble(Upper);
        double limDown = Double.parseDouble(Lower);
        double limStep = Double.parseDouble(Step);
        while (limDown + limStep < limUp) {
            sum += ((Math.exp(-limDown) + Math.exp(-(limDown + limStep))) / 2) * limStep;
            limDown += limStep;
        }
        sum += ((Math.exp(-limDown) + Math.exp(-limUp)) / 2) * limStep;
        return sum;
    }

    List<RecIntegral> listA = new ArrayList();

    public Form() {
        setContentPane(rootPanel);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        createTable();

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                double limUp, limDown, step;
                String str_limUp, str_limDown, str_step;
                str_limUp = input1.getText();
                str_limDown = input2.getText();
                str_step = input3.getText();

                try {
                    limUp = Double.parseDouble(str_limUp);
                    limDown = Double.parseDouble(str_limDown);
                    step = Double.parseDouble(str_step);
                    if (limUp < 0.000001 || limUp > 100000)
                        throw new MyException("???????????????? ???????????????? ???????????????? ??????????????");

                    else if (limDown < 0.000001 || limDown > 100000)
                        throw new MyException("???????????????? ???????????????? ?????????????? ??????????????");
                    else if (limDown > limUp)
                        throw new MyException("???????????? ???????????? ???????????? ???????? ???????????? ????????????????");
                    else if ((limUp - limDown) < step)
                        throw new MyException("?????? ???????????? ???????? ???????????? ?????????????????? ????????????????????????????");

                } catch (MyException e) {
                    ShowMsg(e.msg);
                    return;
                } catch (Exception e) {
                    ShowMsg("?????????????????????? ?????????????? ????????????");
                    return;
                }
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                RecIntegral temp = new RecIntegral();
                temp.setUpper(str_limUp);
                temp.setLower(str_limDown);
                temp.setStep(str_step);

                double sum = Calk(str_limUp, str_limDown, str_step);
                temp.setResult(Double.toString(sum));
                model.addRow(new Object[]{model.getRowCount() + 1, str_limUp, str_limDown, str_step, sum});

                listA.add(temp);
                input1.setText("");
                input2.setText("");
                input3.setText("");
                UpdateWindow();
            }
        });

        delButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int SelectedRow;
                try {
                    SelectedRow = table1.getSelectedRow();
                    if (SelectedRow == -1)
                        throw new Exception();
                } catch (Exception e1) {
                    ShowMsg("???? ?????????????? ???????????? ?? ?????????????? ");
                    return;
                }
                int RowCount = table1.getRowCount();

                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                listA.remove(SelectedRow);
                model.removeRow(SelectedRow);
                table1.setModel(model);
                if (SelectedRow == RowCount - 1) {
                    table1.changeSelection(SelectedRow - 1, 0, false, false);
                } else {
                    table1.changeSelection(SelectedRow, 0, false, false);
                }
                UpdateWindow();
            }
        });

        calkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row;
                try {
                    row = table1.getSelectedRow();
                    if (row == -1)
                        throw new Exception();
                } catch (Exception e1) {
                    ShowMsg("???? ?????????????? ???????????? ?? ?????????????? ");
                    return;
                }
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                RecIntegral temp = listA.get(row);
                double sum = Calk(temp.getUpper(), temp.getLower(), temp.getStep());
                temp.setResult(Double.toString(sum));
                listA.set(row,temp);
                model.setValueAt(sum, row, 4);
                UpdateWindow();
            }
        });

        fillButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                RecIntegral temp;

                for (RecIntegral recIntegral : listA) {
                    temp = recIntegral;
                    model.addRow(new Object[]{model.getRowCount() + 1, recIntegral.getUpper(), recIntegral.getLower(), recIntegral.getStep(), recIntegral.getResult()});
                }
                listA.addAll(listA);
                UpdateWindow();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (table1.getRowCount() == 0)
                        throw new Exception();
                } catch (Exception e1) {
                    ShowMsg("?????????????? ??????????");
                    return;
                }
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                while (model.getRowCount() != 0) {
                    model.removeRow(0);
                }
                UpdateWindow();
            }
        });

        saveTButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("???????????????????? ?? ?????????????????? ????????");
                fc.setFileFilter(new FileNameExtensionFilter("Text Files","txt" ));
                fc.showSaveDialog(null);
                File f = fc.getSelectedFile();

                try {
                    DefaultTableModel model = (DefaultTableModel) table1.getModel();
                    int row = model.getRowCount();
                    int col = model.getColumnCount();

                    FileWriter fw = new FileWriter(f);
                    for (int i = 0; i < row; i++) {
                        for (int j = 0; j < col; j++) {
                            fw.write(model.getValueAt(i, j).toString());
                            fw.write(" ");
                        }
                        fw.write("\n");
                    }
                    fw.close();
                    ShowMsg("???????????????????? ???????????? ??????????????");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    ShowMsg("???????????? ?????? ????????????????????");
                }
            }
        });

        saveBButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("???????????????????? ?? ???????????????? ????????");
                fc.setFileFilter(new FileNameExtensionFilter("Binary Files","bin" ));
                fc.showSaveDialog(null);
                File f = fc.getSelectedFile();
                ArrayList <String> arr = new ArrayList<String>();
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                int row = model.getRowCount();
                int col = model.getColumnCount();

                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        arr.add(model.getValueAt(i,j).toString());
                    }
                }

                try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f))))
                {
                    oos.writeObject(arr);
                    oos.close();
                    ShowMsg("???????????????????? ???????????? ??????????????");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    ShowMsg("???????????? ?????? ????????????????????");
                }
            }
        });

        loadTButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("Text Files","txt" ));
                fc.showOpenDialog(null);
                File f = fc.getSelectedFile();
                try {
                    DefaultTableModel model = (DefaultTableModel) table1.getModel();
                    FileReader fr = new FileReader(f);
                    BufferedReader reader = new BufferedReader(fr);
                    String line;
                    String[] split;
                    RecIntegral temp = new RecIntegral();
                    listA.clear();
                    while(model.getRowCount()!=0)
                        model.removeRow(0);
                    while((line = reader.readLine()) != null) {
                        split = line.split(" ");
                        model.addRow(new Object[]{model.getRowCount() + 1, split[1], split[2], split[3], split[4]});
                        temp.setAll(split[1], split[2], split[3], split[4]);
                        listA.add(temp);
                    }
                    reader.close();
                    fr.close();
                    ShowMsg("???????????????? ???????????? ??????????????");
                    UpdateWindow();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    ShowMsg("???????????? ?????? ????????????????");
                }
            }
        });

        loadBButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("Binary Files","bin" ));
                fc.showOpenDialog(null);
                File f = fc.getSelectedFile();
                try {
                    DefaultTableModel model = (DefaultTableModel) table1.getModel();
                    ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream( new FileInputStream(f)));
                    ArrayList <String> arr = (ArrayList<String>)ois.readObject();
                    ois.close();
                    listA.clear();
                    while(model.getRowCount()!=0)
                        model.removeRow(0);
                    for (int i=0;i<arr.size();i+=5) {
                        RecIntegral recint = new RecIntegral();
                        recint.setAll(arr.get(i+1), arr.get(i+2), arr.get(i+3), arr.get(i+4));
                        model.addRow(new Object[]{model.getRowCount() + 1, arr.get(i+1), arr.get(i+2), arr.get(i+3), arr.get(i+4)});
                        listA.add(recint);
                    }
                    ShowMsg("???????????????? ???????????? ??????????????");
                    UpdateWindow();
                } catch (IOException | ClassNotFoundException ioException) {
                    ioException.printStackTrace();
                    ShowMsg("???????????? ?????? ????????????????");
                }
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        rootPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createTable() {

        table1.setModel(new DefaultTableModel(
                null,
                new String[]{
                        "#", "?????????????? ?????????????? ????????????????????????????", "???????????? ?????????????? ????????????????????????????",
                        "?????? ????????????????????????????", "??????????????????"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 4;
            }
        });
    }

    private void ShowMsg(String s) {
        this.setVisible(true);
        JOptionPane.showMessageDialog(null, s);
        this.setVisible(true);
    }

    private void UpdateWindow() {
        this.setVisible(true);
    }

    public static void main(String[] args) {
        Form dialog = new Form();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

}
