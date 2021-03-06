/*
 * Dateiname: GenderDialog.java
 * Projekt  : WollMux
 * Funktion : Erlaubt die Bearbeitung der Funktion eines Gender-Feldes.
 * 
 * Copyright (c) 2008-2019 Landeshauptstadt München
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the European Union Public Licence (EUPL),
 * version 1.0 (or any later version).
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 *
 * You should have received a copy of the European Union Public Licence
 * along with this program. If not, see
 * http://ec.europa.eu/idabc/en/document/7330
 *
 * Änderungshistorie:
 * Datum      | Wer | Änderungsgrund
 * -------------------------------------------------------------------
 * 21.02.2008 | LUT | Erstellung als GenderDialog
 * -------------------------------------------------------------------
 *
 * Christoph lutz (D-III-ITD 5.1)
 * @version 1.0
 * 
 */
package de.muenchen.allg.itd51.wollmux.dialog.trafo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.awt.XButton;
import com.sun.star.awt.XComboBox;
import com.sun.star.awt.XContainerWindowProvider;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.uno.UnoRuntime;

import de.muenchen.allg.afid.UNO;
import de.muenchen.allg.itd51.wollmux.core.dialog.adapter.AbstractActionListener;
import de.muenchen.allg.itd51.wollmux.core.parser.ConfigThingy;
import de.muenchen.allg.itd51.wollmux.core.parser.NodeNotFoundException;

/**
 * Erlaubt die Bearbeitung der Funktion eines Gender-Feldes.
 * 
 * @author Christoph Lutz (D-III-ITD 5.1)
 */
public class GenderDialog
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GenderDialog.class);

  private TrafoDialogParameters params;

  private XControlContainer controlContainer;
  
  private XDialog dialog;

  public GenderDialog(TrafoDialogParameters params)
  {
    this.params = params;
    // if (!params.isValid || params.conf == null || params.fieldNames == null
    // || params.fieldNames.size() == 0) throw new IllegalArgumentException();

    ConfigThingy conf = params.conf;

    /*
     * Parsen von conf mit dem Aufbau Func(BIND(FUNCTION "Gender" SET("Anrede", VALUE "<anredeFieldId>")
     * SET("Falls_Anrede_HerrN", "<textHerr>") SET("Falls_Anrede_Frau", "<textFrau>")
     * SET("Falls_sonstige_Anrede", "<textSonst>")))
     */
    try
    {
      String textHerr = null;
      String textFrau = null;
      String textSonst = null;
      String anredeId = null;

      // if (conf.count() != 1) stop();
      ConfigThingy bind = conf.getFirstChild();
      // if (!bind.getName().equals("BIND")) stop();

      ConfigThingy funcs = bind.query("FUNCTION", 1);
      // if (funcs.count() != 1) stop();
      ConfigThingy func = funcs.getLastChild();
      // if (!func.toString().equals("Gender")) stop();

      for (ConfigThingy set : bind)
      {
        if (!set.getName().equals("SET") || set.count() != 2) continue;
        String setKey = set.getFirstChild().toString();
        ConfigThingy value = set.getLastChild();

        if (setKey.equals("Anrede") && value.getName().equals("VALUE")
          && value.count() == 1) anredeId = value.toString();
        if (setKey.equals("Falls_Anrede_HerrN") && value.count() == 0)
          textHerr = value.getName();
        if (setKey.equals("Falls_Anrede_Frau") && value.count() == 0)
          textFrau = value.getName();
        if (setKey.equals("Falls_sonstige_Anrede") && value.count() == 0)
          textSonst = value.getName();
      }

      HashSet<String> uniqueFieldNames = new HashSet<>(params.fieldNames);
      uniqueFieldNames.add(anredeId);
      List<String> sortedNames = new ArrayList<>(uniqueFieldNames);
      Collections.sort(sortedNames);

      buildGUI(anredeId, textHerr, textFrau, textSonst, sortedNames);
    }
    catch (NodeNotFoundException e)
    {
      LOGGER.error("", e);
    }
  }


  /**
   * Baut das genderPanel auf.
   * 
   * @author Christoph Lutz (D-III-ITD-5.1)
   */
  private void buildGUI(String anredeId, String textHerr, String textFrau,
      String textSonst, final List<String> fieldNames)
  {
    XWindowPeer peer = UNO.XWindowPeer(UNO.desktop.getCurrentFrame().getContainerWindow());
    XContainerWindowProvider provider = null;

    try
    {
      provider = UnoRuntime.queryInterface(XContainerWindowProvider.class,
          UNO.xMCF.createInstanceWithContext("com.sun.star.awt.ContainerWindowProvider",
              UNO.defaultContext));
    } catch (com.sun.star.uno.Exception e)
    {
      LOGGER.error("", e);
    }

    XWindow window = provider.createContainerWindow(
        "vnd.sun.star.script:WollMux.gender_dialog?location=application", "", peer, null);
    controlContainer = UnoRuntime.queryInterface(XControlContainer.class, window);

    XComboBox cbAnrede = UNO.XComboBox(controlContainer.getControl("cbSerienbrieffeld"));
    cbAnrede.addItems(fieldNames.toArray(new String[fieldNames.size()]), (short) 0);

    XTextComponent txtFemale = UNO.XTextComponent(controlContainer.getControl("txtFemale"));
    txtFemale.setText(textFrau);

    XTextComponent txtMale = UNO.XTextComponent(controlContainer.getControl("txtMale"));
    txtMale.setText(textHerr);

    XTextComponent txtOther = UNO.XTextComponent(controlContainer.getControl("txtOthers"));
    txtOther.setText(textSonst);
    
    XButton btnAbort = UNO.XButton(controlContainer.getControl("btnAbort"));
    btnAbort.addActionListener(btnAbortActionListener);
    
    XButton btnOK = UNO.XButton(controlContainer.getControl("btnOK"));
    btnOK.addActionListener(btnOKActionListener);
    
    dialog = UnoRuntime.queryInterface(XDialog.class, window);
    dialog.execute();

  }
  
  private AbstractActionListener btnAbortActionListener = event -> {
    dialog.endExecute();
  };
  
  private AbstractActionListener btnOKActionListener = event -> {
    updateTrafoConf();
    dialog.endExecute();
  };

  /**
   * Aktualisiert {@link #params},conf anhand des aktuellen Dialogzustandes und
   * setzt params,isValid auf true.
   * 
   */
  private void updateTrafoConf()
  {
    params.conf = new ConfigThingy(params.conf.getName());
    params.conf.addChild(generateGenderTrafoConf(
      UNO.XTextComponent(controlContainer.getControl("cbSerienbrieffeld")).getText(),
      UNO.XTextComponent(controlContainer.getControl("txtMale")).getText(), 
      UNO.XTextComponent(controlContainer.getControl("txtFemale")).getText(),
      UNO.XTextComponent(controlContainer.getControl("txtOthers")).getText()));
    params.isValid = true;
  }

  /**
   * Erzeugt ein ConfigThingy mit dem Aufbau BIND(FUNCTION "Gender" SET("Anrede",
   * VALUE "<anredeFieldId>") SET("Falls_Anrede_HerrN", "<textHerr>")
   * SET("Falls_Anrede_Frau", "<textFrau>") SET("Falls_sonstige_Anrede", "<textSonst>"))
   * 
   * @param anredeId
   *          Id des geschlechtsbestimmenden Feldes
   * @param textHerr
   *          Text für Herr
   * @param textFrau
   *          Text für Frau
   * @param textSonst
   *          Text für sonstige Anreden
   * 
   * @author Christoph Lutz (D-III-ITD-5.1)
   */
  public static ConfigThingy generateGenderTrafoConf(String anredeId,
      String textHerr, String textFrau, String textSonst)
  {
    ConfigThingy bind = new ConfigThingy("BIND");
    bind.add("FUNCTION").add("Gender");

    ConfigThingy setAnrede = bind.add("SET");
    setAnrede.add("Anrede");
    setAnrede.add("VALUE").add(anredeId);

    ConfigThingy setHerr = bind.add("SET");
    setHerr.add("Falls_Anrede_HerrN");
    setHerr.add(textHerr);

    ConfigThingy setFrau = bind.add("SET");
    setFrau.add("Falls_Anrede_Frau");
    setFrau.add(textFrau);

    ConfigThingy setSonst = bind.add("SET");
    setSonst.add("Falls_sonstige_Anrede");
    setSonst.add(textSonst);

    return bind;
  }
}
