package de.muenchen.allg.itd51.wollmux.event.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.text.XTextCursor;

import de.muenchen.allg.itd51.wollmux.TextModule;
import de.muenchen.allg.itd51.wollmux.WollMuxFehlerException;
import de.muenchen.allg.itd51.wollmux.core.util.L;
import de.muenchen.allg.itd51.wollmux.dialog.InfoDialog;
import de.muenchen.allg.itd51.wollmux.document.TextDocumentController;
import de.muenchen.allg.itd51.wollmux.event.WollMuxEventHandler;

/**
 * Erzeugt ein neues WollMuxEvent, das signasisiert, das ein Textbaustein über den
 * Textbaustein-Bezeichner direkt ins Dokument eingefügt wird. Mit reprocess wird
 * übergeben, wann die Dokumentenkommandos ausgewertet werden soll. Mir reprocess =
 * true sofort.
 *
 * Das Event wird von WollMux.dispatch(...) geworfen z.B über Druck eines
 * Tastenkuerzels oder Druck auf den Knopf der OOo-Symbolleiste ein
 * "wollmux:TextbausteinEinfuegen" dispatch erfolgte.
 */
public class OnTextbausteinEinfuegen extends BasicEvent
{

  private static final Logger LOGGER = LoggerFactory.getLogger(OnTextbausteinEinfuegen.class);

  private boolean reprocess;
  private TextDocumentController documentController;

  public OnTextbausteinEinfuegen(TextDocumentController documentController,
      boolean reprocess)
  {
    this.documentController = documentController;
    this.reprocess = reprocess;

  }

  @Override
  protected void doit()
  {
    XTextCursor viewCursor = documentController.getModel().getViewCursor();
    try
    {
      TextModule.createInsertFragFromIdentifier(
          documentController.getModel().doc, viewCursor, reprocess);
      if (reprocess)
        WollMuxEventHandler.getInstance()
            .handleReprocessTextDocument(documentController);
      if (!reprocess)
        InfoDialog.showInfoModal(L.m("WollMux"),
            L.m("Der Textbausteinverweis wurde eingefügt."));
    } catch (WollMuxFehlerException e)
    {
      LOGGER.error("Textbausteinverweis konnte nicht eingefügt werden.", e);
      InfoDialog.showInfoModal(L.m("WollMux-Fehler"), "Textbausteinverweis konnte nicht eingefügt werden.");
    }
  }

  @Override
  public String toString()
  {
    return this.getClass().getSimpleName() + "(" + documentController.getModel()
        + ", " + reprocess + ")";
  }
}