package de.muenchen.allg.itd51.wollmux;

import de.muenchen.allg.itd51.wollmux.core.dialog.DialogLibrary;
import de.muenchen.allg.itd51.wollmux.core.functions.FunctionFactory;
import de.muenchen.allg.itd51.wollmux.core.functions.FunctionLibrary;
import de.muenchen.allg.itd51.wollmux.dialog.DialogFactory;
import de.muenchen.allg.itd51.wollmux.func.StandardPrint;
import de.muenchen.allg.itd51.wollmux.print.PrintFunctionLibrary;

public class GlobalFunctions
{
  private static GlobalFunctions instance;
  
  /**
   * Enthält die im Funktionen-Abschnitt der wollmux,conf definierten Funktionen.
   */
  private FunctionLibrary globalFunctions;
  /**
   * Enthält die im Dokumentaktionen der wollmux,conf definierten Funktionen.
   */
  private FunctionLibrary documentActionFunctions;
  /**
   * Enthält die im Funktionsdialoge-Abschnitt der wollmux,conf definierten Dialoge.
   */
  private DialogLibrary funcDialogs;
  /**
   * Enthält die im Funktionen-Abschnitt der wollmux,conf definierten Funktionen.
   */
  private PrintFunctionLibrary globalPrintFunctions;
  
  public static GlobalFunctions getInstance()
  {
    if (instance == null)
      instance = new GlobalFunctions();
    
    return instance;
  }

  private GlobalFunctions()
  {
    /*
     * Globale Funktionsdialoge parsen. ACHTUNG! Muss vor parseGlobalFunctions() erfolgen. Als
     * context wird null übergeben, weil globale Funktionen keinen Kontext haben.
     */
    funcDialogs =
      DialogFactory.parseFunctionDialogs(WollMuxFiles.getWollmuxConf(), null, null);

    /*
     * Globale Funktionen parsen. ACHTUNG! Verwendet die Funktionsdialoge. Diese
     * müssen also vorher geparst sein. Als context wird null übergeben, weil globale
     * Funktionen keinen Kontext haben.
     */
    globalFunctions =
      FunctionFactory.parseFunctions(WollMuxFiles.getWollmuxConf(),
        getFunctionDialogs(), null, null);

    /*
     * Globale Druckfunktionen parsen.
     */
    globalPrintFunctions =
      PrintFunctionLibrary.parsePrintFunctions(WollMuxFiles.getWollmuxConf());
    StandardPrint.addInternalDefaultPrintFunctions(globalPrintFunctions);

    /*
     * Dokumentaktionen parsen. Diese haben weder Kontext noch Dialoge.
     */
    documentActionFunctions = new FunctionLibrary(null, true);
    FunctionFactory.parseFunctions(documentActionFunctions,
      WollMuxFiles.getWollmuxConf(), "Dokumentaktionen", null, null);
  }
  
  /**
   * Liefert die Funktionsbibliothek, die die global definierten Funktionen enthält.
   */
  public FunctionLibrary getGlobalFunctions()
  {
    return globalFunctions;
  }

  /**
   * Liefert die Funktionsbibliothek, die die Dokumentaktionen enthält.
   */
  public FunctionLibrary getDocumentActionFunctions()
  {
    return documentActionFunctions;
  }

  /**
   * Liefert die Funktionsbibliothek, die die global definierten Druckfunktionen
   * enthält.
   */
  public PrintFunctionLibrary getGlobalPrintFunctions()
  {
    return globalPrintFunctions;
  }

  /**
   * Liefert die Dialogbibliothek, die die Dialoge enthält, die in Funktionen
   * (Grundfunktion "DIALOG") verwendung finden.
   */
  public DialogLibrary getFunctionDialogs()
  {
    return funcDialogs;
  }

}
