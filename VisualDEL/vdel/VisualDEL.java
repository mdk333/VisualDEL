package vdel;


import vdel.utilities.Utilities;
import vdel.parser.BaseParser;
import vdel.scanners.BaseScanner;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.swing.event.MenuDragMouseEvent;
import org.apache.commons.lang3.StringUtils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author knight
 */
public class VisualDEL {

    public static void main(String[] args) {
//        try {
//               // Utilities.dotFileName = StringUtils.stripEnd(args[i], ".vdel").concat(".dot");
//                BaseParser p = new BaseParser(new BaseScanner(new FileReader("sum_and_product_tempered.vdel")));
//                Object result = p.parse().value;
//                
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        for (int i = 0; i < args.length; i++) {
            // String string = args[i];
            try {
                Utilities.dotFileName = StringUtils.stripEnd(args[i], ".vdel").concat(".dot");
                BaseParser p = new BaseParser(new BaseScanner(new FileReader(args[i])));
                Object result = p.parse().value;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            

        }
        
                try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EpistemicKeypad.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EpistemicKeypad.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EpistemicKeypad.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EpistemicKeypad.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EpistemicKeypad().setVisible(true);
            }
        });
//        new EpistemicKeypad().setVisible(true);
       // HashSet<Integer> hs = new HashSet<Integer>();
       // hs.contains(hs);
        
//        Iterator itSymbols = Utilities.symbolTable.entrySet().iterator();

//        while (itSymbols.hasNext()) {
//            Map.Entry variable = (Map.Entry) itSymbols.next();
//        //    String[] vals = ((ArrayList<String>) variable.getValue()).toArray(new String[0]);
//        //    if (!vals[0].equalsIgnoreCase("dynamic")) {  //only create partition for non-dynamic variables because at this point the dynamic variables have no values yet
//                for (int i = 2; i < vals.length; i++) {  // i starts at 2 because 0 contains the type of the variable, 1 contains the position of that variable in the (to be generated) label, 2 onwards contains the possible values for this variable
//                    elementsList.add(vals[i]);
//                    endIndex++;
//                }
//          //      partitions[partitionIndex] = startIndex + "," + endIndex;
//          //      partitionIndex++;
//          //      startIndex = endIndex + 1;
//          //  }
//        }
       // StringBuilder sb = new StringBuilder();

  //    double no = 5%3;
        
    }
}
