/*
* Dateiname: AllFormControlModelLineViewsPanel.java
* Projekt  : WollMux
* Funktion : H�lt in einem Panel FormControlModelLineViews f�r alle FormControlModels einer FormControlModelList.
* 
* Copyright: Landeshauptstadt M�nchen
*
* �nderungshistorie:
* Datum      | Wer | �nderungsgrund
* -------------------------------------------------------------------
* 30.08.2006 | BNK | Erstellung
* 10.09.2006 | BNK | [R3208]Tab-Struktur des Formulars bereits im FM4000 anzeigen
* -------------------------------------------------------------------
*
* @author Matthias Benkmann (D-III-ITD 5.1)
* @version 1.0
* 
*/
package de.muenchen.allg.itd51.wollmux.former;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import de.muenchen.allg.itd51.wollmux.Logger;
import de.muenchen.allg.itd51.wollmux.former.FormControlModelList.ItemListener;

/**
 * H�lt in einem Panel FormControlModelLineViews f�r alle 
 * {@link de.muenchen.allg.itd51.wollmux.former.FormControlModel} einer 
 * {@link de.muenchen.allg.itd51.wollmux.former.FormControlModelList}.
 *
 * @author Matthias Benkmann (D-III-ITD 5.1)
 */
public class AllFormControlModelLineViewsPanel implements View, ItemListener, OneFormControlLineView.ViewChangeListener
{
  /**
   * Rand um Textfelder (wird auch f�r ein paar andere R�nder verwendet)
   * in Pixeln.
   */
  private final static int TF_BORDER = 4;
  
  /**
   * Rand um Buttons (in Pixeln).
   */
  private final static int BUTTON_BORDER = 2;
  
  /**
   * Wird (mit wechselndem gridy Wert) verwendet als Constraints f�r das Hinzuf�gen von
   * Views zum Panel. 
   */
  private GridBagConstraints gbcLineView = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START,   GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0);
  
  /**
   * Wird verwendet als Constraints f�r das Hinzuf�gen von Glues zu den Tabs.
   */
  private GridBagConstraints gbcBottomGlue = new GridBagConstraints(0, FormControlModelList.MAX_MODELS_PER_TAB + 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,   GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0);
  
  /**
   * Die {@link FormControlModelList}, deren Inhalt in dieser View angezeigt wird.
   */
  private FormControlModelList formControlModelList;
  
  /**
   * Das Panel, das alle Komponenten dieser View enth�lt.
   */
  private JPanel myPanel;
  
  private JTabbedPane tabPane;
  
  /**
   * Das Panel, das die ganzen {@link OneFormControlLineView}s enth�lt.
   */
  private JPanel firstTab;
  
  /**
   * Enth�lt die {@link ViewDescriptor}s in der richtigen Reihenfolge.
   */
  private Vector viewDescriptors = new Vector();

  /**
   * Ein Vector von Integers, die die Indizes der selektierten Views angeben.
   */
  private Vector selection = new Vector();
  
  /**
   * Die Scrollpane in der sich die {@link #tabPane} befindet.
   */
  private JScrollPane scrollPane;
  
  /**
   * Der FormularMax4000, zu dem diese View geh�rt.
   */
  private FormularMax4000 formularMax4000;
  
  /**
   * Erzeugt eine AllFormControlModelLineViewsPanel, die den Inhalt von
   * formControlModelList anzeigt. ACHTUNG! formControlModelList sollte leer sein,
   * da nur neu hinzugekommene Elemente in der View angezeigt werden.
   * @author Matthias Benkmann (D-III-ITD 5.1)
   * TESTED*/
  public AllFormControlModelLineViewsPanel(FormControlModelList formControlModelList, FormularMax4000 formularMax4000)
  {
    this.formControlModelList = formControlModelList;
    this.formularMax4000 = formularMax4000;
    formControlModelList.addListener(this);
    formularMax4000.addBroadcastListener(new MyBroadcastListener());

    myPanel = new JPanel(new GridBagLayout());
    tabPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    firstTab = new JPanel(new GridBagLayout());
    tabPane.addTab("Tab", firstTab);
    firstTab.add(Box.createGlue(), gbcBottomGlue);
    
    scrollPane = new JScrollPane(tabPane);
    scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
    
     //    int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor,          int fill,                  Insets insets, int ipadx, int ipady)
    GridBagConstraints gbcMainPanel = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,       new Insets(0,0,0,0),0,0);
    gbcMainPanel.gridx = 0;
    gbcMainPanel.gridy = 0;
    
    myPanel.add(scrollPane, gbcMainPanel);
    
    JPanel buttonPanel = new JPanel(new GridBagLayout());
    //int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor,          int fill,                  Insets insets, int ipadx, int ipady)
    GridBagConstraints gbcButtonPanel = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,       new Insets(TF_BORDER,TF_BORDER,TF_BORDER,TF_BORDER),0,0);
    gbcButtonPanel.gridx = 0;
    gbcButtonPanel.gridy = 2;
    myPanel.add(buttonPanel,gbcButtonPanel);
    
    GridBagConstraints gbcButton = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,       new Insets(BUTTON_BORDER,BUTTON_BORDER,BUTTON_BORDER,BUTTON_BORDER),0,0);
    JButton hochButton = new JButton("Hoch");
    hochButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if (noSelectedElementsOnVisibleTab()) return;
        moveSelectedElementsUp();
        if (noSelectedElementsOnVisibleTab()) showSelection();
      }});
    buttonPanel.add(hochButton, gbcButton);
    
    ++gbcButton.gridx;
    JButton runterButton = new JButton("Runter");
    runterButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if (noSelectedElementsOnVisibleTab()) return;
        moveSelectedElementsDown();
        if (noSelectedElementsOnVisibleTab()) showSelection();
      }});
    buttonPanel.add(runterButton, gbcButton);
    
    ++gbcButton.gridx;
    JButton killButton = new JButton("L�schen");
    killButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if (noSelectedElementsOnVisibleTab()) return;
        deleteSelectedElements();
      }});
    buttonPanel.add(killButton, gbcButton);
    
    ++gbcButton.gridx;
    JButton tabButton = new JButton("Tab");
    tabButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if (noSelectedElementsOnVisibleTab()) return;
        insertNewTab();
        if (noSelectedElementsOnVisibleTab()) showSelection();
      }});
    buttonPanel.add(tabButton, gbcButton);
    
    ++gbcButton.gridx;
    JButton newButton = new JButton("Neu");
    newButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        insertNewElement();
      }});
    buttonPanel.add(newButton, gbcButton);
  }

  public JComponent JComponent()
  {
    return myPanel;
  }

  //TESTED
  public void itemAdded(FormControlModel model, int index)
  {
    if (index < 0 || index > viewDescriptors.size()) 
    {
      Logger.error("Inkonsistenz zwischen Model und View!");
      return;
    }
    OneFormControlLineView ofclView = new OneFormControlLineView(model, this, formularMax4000);
    
    boolean isTab = (model.getType() == FormControlModel.TAB_TYPE);
    JComponent tab = firstTab;
    int tabIndex = 0;
    int gridY = 0;
    if (viewDescriptors.size() > 0)
    {
      int i = index;
      if (i == viewDescriptors.size()) --i;
      ViewDescriptor desc = (ViewDescriptor)viewDescriptors.get(i);
      tabIndex = desc.containingTabIndex;
      tab = (JComponent)tabPane.getComponentAt(tabIndex);
      gridY = desc.gridY;
      if (i != index) ++gridY;
    }
    
    ViewDescriptor desc = new ViewDescriptor(ofclView, gridY, tabIndex, isTab);
    viewDescriptors.add(index, desc);
    
    gbcLineView.gridy = gridY;
    tab.add(ofclView.JComponent(), gbcLineView);
    tab.validate();
    
    fixTabStructure(); tab = null; //tab ist eventuell entfernt worden!
    
    tabPane.validate();
    scrollPane.validate();
    
    fixupSelectionIndices(index, 1);
  }
  
  /**
   * Geht die {@link #viewDescriptors} Liste durch und behebt Fehler, die durch strukturelle
   * �nderungen hervorgerufen wurden. Dabei werden auch neue Tabs in {@link #tabPane} angelegt
   * bzw. alte entfernt wenn n�tig.
   * 
   * @author Matthias Benkmann (D-III-ITD 5.1)
   * TESTED
   */
  private void fixTabStructure()
  {
    Set toValidate = new HashSet();
    int gridY = 0;
    int tabIndex = 0;
    JComponent tab = firstTab;
    Iterator iter = viewDescriptors.iterator();
    while (iter.hasNext())
    {
      ViewDescriptor desc = (ViewDescriptor)iter.next();
      if (desc.isTab && gridY > 0)
      {
        ++tabIndex;
        gridY = 0;
        if (tabIndex >= tabPane.getTabCount())
        {
          JPanel newTab = new JPanel(new GridBagLayout());
          newTab.add(Box.createGlue(), gbcBottomGlue);
          tabPane.addTab("Tab", newTab);
        }
        
        tab = (JComponent)tabPane.getComponentAt(tabIndex);
      }
      
      if (desc.containingTabIndex != tabIndex || desc.gridY != gridY)
      {
        JComponent oldTab = (JComponent)tabPane.getComponent(desc.containingTabIndex);
        oldTab.remove(desc.view.JComponent());
        gbcLineView.gridy = gridY;
        tab.add(desc.view.JComponent(), gbcLineView);
        desc.containingTabIndex = tabIndex;
        desc.gridY = gridY;
        toValidate.add(tab);
        toValidate.add(oldTab);
      }
      
      if (desc.isTab)
        tabPane.setTitleAt(desc.containingTabIndex, desc.view.getModel().getLabel());
      
      ++gridY;
    }
    
    ++tabIndex;
    while (tabIndex < tabPane.getTabCount()) tabPane.removeTabAt(tabPane.getTabCount() - 1);
    
    iter = toValidate.iterator();
    while (iter.hasNext())
    {
      ((JComponent)iter.next()).validate();
    }
  }

  /**
   * Liefert den Index des ViewDescriptors, der zu view geh�rt in der Liste
   * {@link #viewDescriptors} oder -1, wenn die view nicht enthalten ist.
   * @author Matthias Benkmann (D-III-ITD 5.1)
   */
  private int getDescriptorIndexOf(OneFormControlLineView view)
  {
    for (int i = 0; i < viewDescriptors.size(); ++i)
    {
      if (((ViewDescriptor)viewDescriptors.get(i)).view == view) return i;
    }
    
    return -1;
  }
  
  public void viewShouldBeRemoved(OneFormControlLineView view)
  { //TESTED
    int index = getDescriptorIndexOf(view);
    if (index < 0) return;
    
    ViewDescriptor desc = (ViewDescriptor)viewDescriptors.remove(index);
    JComponent tab = (JComponent)tabPane.getComponentAt(desc.containingTabIndex); 
    tab.remove(view.JComponent());
    tab.validate();
    
    fixTabStructure(); tab = null; //tab ist eventuell entfernt worden!
    
    tabPane.validate();
    scrollPane.validate();
    
    removeSelectionIndex(index);
    fixupSelectionIndices(index, -1);
  }
  
  public void tabTitleChanged(OneFormControlLineView view)
  {
    int index = getDescriptorIndexOf(view);
    if (index < 0) return;
    ViewDescriptor desc = (ViewDescriptor)viewDescriptors.get(index);
    tabPane.setTitleAt(desc.containingTabIndex, view.getModel().getLabel());
  }

  /**
   * Schiebt alle ausgew�hlten Elemente um einen Platz nach oben, d,h, in Richtung niedrigerer
   * Indizes. Falls Element 0 ausgew�hlt ist wird gar nichts getan.
   * 
   * @author Matthias Benkmann (D-III-ITD 5.1)
   * TESTED
   */
  private void moveSelectedElementsUp()
  {
    if (((Integer)selection.firstElement()).intValue() > 0)
    {
      formControlModelList.moveElementsUp(selection);
      //Kein fixupSelectionIndices(0, -1) n�tig, weil die itemSwapped() Events schon daf�r sorgen
    }
  }
  
  /**
   * Schiebt alle ausgew�hlten Elemente um einen Platz nach unten, d,h, in Richtung niedrigerer
   * Indizes. Falls das letzte Element ausgew�hlt ist wird gar nichts getan.
   * 
   * @author Matthias Benkmann (D-III-ITD 5.1)
   * TESTED
   */
  private void moveSelectedElementsDown()
  {
    if (((Integer)selection.lastElement()).intValue() < viewDescriptors.size() - 1)
    {
      formControlModelList.moveElementsDown(selection);
     //Kein fixupSelectionIndices(0, 1) n�tig, weil die itemSwapped() Events schon daf�r sorgen
    }
  }
  
  /**
   * L�scht alle ausgew�hlten Elemente.
   * 
   * @author Matthias Benkmann (D-III-ITD 5.1)
   * TESTED
   */
  private void deleteSelectedElements()
  {
    /**
     * Die folgende Schleife muss auf diese Weise geschrieben werden und nicht mit einem
     * Iterator, weil es ansonsten eine ConcurrentModificationException gibt, da
     * �ber {@link OneFormControlLineView.ViewChangeListener#viewShouldBeRemoved(OneFormControlLineView)}
     * die Selektion w�hrend des remove() gleich ver�ndert wird, was den Iterator
     * invalidieren w�rde.
     */
    while (!selection.isEmpty())
    {
      Integer I = (Integer)selection.lastElement();
      ViewDescriptor desc = (ViewDescriptor)viewDescriptors.get(I.intValue());
      formControlModelList.remove(desc.view.getModel());
    }
  }
  
  /**
   * Liefert true gdw sich kein ausgew�hltes Element auf dem momentan angezeigten Tab
   * befindet.
   * @author Matthias Benkmann (D-III-ITD 5.1)
   */
  private boolean noSelectedElementsOnVisibleTab()
  {
    int tabIndex = tabPane.getSelectedIndex();
    Iterator iter = selection.iterator();
    while (iter.hasNext())
    {
      int i = ((Integer)iter.next()).intValue();
      ViewDescriptor desc = (ViewDescriptor)viewDescriptors.get(i);
      if (desc.containingTabIndex == tabIndex) return false;
    }
    return true;
  }
  
  /**
   * Falls mindestens ein Element ausgew�hlt ist wird das sichtbare Tab so gesetzt,
   * dass das erste ausgew�hlte Element angezeigt wird.
   * 
   * @author Matthias Benkmann (D-III-ITD 5.1)
   */
  private void showSelection()
  {
    if (selection.isEmpty()) return;
    
    int i = ((Integer)selection.firstElement()).intValue();
    ViewDescriptor desc = (ViewDescriptor)viewDescriptors.get(i);
    tabPane.setSelectedIndex(desc.containingTabIndex);
  }
  
  /**
   * Liefert den Index des ersten momentan sichtbaren vom Benutzer ausgew�hlten Elements
   * oder (falls kein ausgew�hltes Element sichtbar ist) den Index des letzten sichtbaren
   * Elements + 1 oder falls kein Tab ausgew�hlt ist dann -1.
   * @author Matthias Benkmann (D-III-ITD 5.1)
   */
  public int getInsertionIndex()
  {
    int tabIndex = tabPane.getSelectedIndex();
    
    /*
     * Zuerst suchen wir die Selektion durch und liefern falls vorhanden das
     * erste ausgew�hlte Element, das auf dem sichtbaren Tab ist.
     */
    Iterator iter = selection.iterator();
    while (iter.hasNext())
    {
      int i = ((Integer)iter.next()).intValue();
      ViewDescriptor desc = (ViewDescriptor)viewDescriptors.get(i);
      if (desc.containingTabIndex == tabIndex) return i;
    }
    
    /*
     * Wenn die obige Suche fehlschl�gt wird getButtonInsertionIndex() verwendet.
     */
    return getButtonInsertionIndex();
  }

  /**
   * Liefert den Index an dem Buttons auf dem aktuell sichtbaren Tab eingef�gt
   * werden sollten oder -1, falls kein Tab ausgew�hlt ist. Der zur�ckgelieferte
   * Wert (falls nicht -1) entspricht dem Index des letzten sichtbaren
   * Elements + 1.
   * @author Matthias Benkmann (D-III-ITD 5.1)
   */
  public int getButtonInsertionIndex()
  {
    int tabIndex = tabPane.getSelectedIndex();
    for (int i = 0; i < viewDescriptors.size(); ++i)
    {
      ViewDescriptor desc = (ViewDescriptor)viewDescriptors.get(i);
      if (desc.containingTabIndex > tabIndex)
        return i;
    }

    return -1;
  }
  
  /**
   * F�gt vor dem ersten ausgew�hlten Element (oder ganz am Ende, wenn nichts ausgew�hlt ist)
   * ein neues Tab zur Liste hinzu.
   * 
   * @author Matthias Benkmann (D-III-ITD 5.1)
   * TESTED
   */
  private void insertNewTab()
  {
    String id = formControlModelList.makeUniqueId(FormularMax4000.STANDARD_TAB_NAME);
    String label = id;
    FormControlModel model = FormControlModel.createTab(label, id);
    int index = getInsertionIndex();
    formControlModelList.add(model, index);
  }

  /**
   * F�gt vor dem ersten ausgew�hlten Element (oder ganz am Ende des sichtbaren Tabs, 
   * wenn nichts ausgew�hlt ist oder die Selektion auf einem unsichtbaren Tab ist)
   * ein neues Steuerelement zur Liste hinzu.
   * 
   * @author Matthias Benkmann (D-III-ITD 5.1)
   * TESTED
   */
  private void insertNewElement()
  {
    String id = formControlModelList.makeUniqueId("Eingabe");
    String label = id;
    FormControlModel model = FormControlModel.createTextfield(label, id);
    int index = getInsertionIndex();
    formControlModelList.add(model, index);
  }
  
  public void itemSwapped(int index1, int index2)
  {
    ViewDescriptor desc1 = (ViewDescriptor)viewDescriptors.get(index1);
    ViewDescriptor desc2 = (ViewDescriptor)viewDescriptors.get(index2);
    viewDescriptors.setElementAt(desc1, index2);
    viewDescriptors.setElementAt(desc2, index1);
    
    fixTabStructure();
    
    tabPane.validate();
    scrollPane.validate();
    
    swapSelectionIndices(index1, index2);
  }
  
  /**
   * Addiert auf alle Indizes in der {@link #selection} Liste gr��er gleich start den offset.
   * Indizes die dabei illegal werden werden aus der Liste gel�scht.
   * @author Matthias Benkmann (D-III-ITD 5.1)
   * TESTED*/
  private void fixupSelectionIndices(int start, int offset)
  {
    ListIterator iter = selection.listIterator();
    while (iter.hasNext())
    {
      Integer I = (Integer)iter.next();
      int i = I.intValue();
      if (i >= start)
      {
        i = i + offset;
        if (i < 0 || i > viewDescriptors.size()-1)
          iter.remove();
        else 
          iter.set(new Integer(i));
      }
    }
  }
  
  /**
   * Ersetzt in {@link #selection} index1 durch index2 und index2 durch index1.
   * @author Matthias Benkmann (D-III-ITD 5.1)
   * TESTED
   */
  private void swapSelectionIndices(int index1, int index2)
  {
    ListIterator iter = selection.listIterator();
    while (iter.hasNext())
    {
      Integer I = (Integer)iter.next();
      int i = I.intValue();
      if (i == index1)
        iter.set(new Integer(index2));
      else if (i == index2)
        iter.set(new Integer(index1));
    }
    
    Collections.sort(selection);
  }
  
  /**
   * Hebt die Selektion aller Elemente auf.
   * 
   * @author Matthias Benkmann (D-III-ITD 5.1)
   * TESTED */
  private void clearSelection()
  {
    Iterator iter = selection.iterator();
    while (iter.hasNext())
    {
      Integer I = (Integer)iter.next();
      OneFormControlLineView view = ((ViewDescriptor)viewDescriptors.get(I.intValue())).view;
      view.unmark();
    }
    selection.clear();
  }
  
  /**
   * Entfernt den Index i aus der {@link #selection} Liste falls er dort enthalten ist.
   * @author Matthias Benkmann (D-III-ITD 5.1)
   * TESTED*/
  private void removeSelectionIndex(int i)
  {
    int idx = selection.indexOf(new Integer(i));
    if (idx >= 0) selection.remove(idx);
  }

  private class MyBroadcastListener extends BroadcastListener
  {
    public void broadcastFormControlModelSelection(BroadcastFormControlModelSelection b) 
    { //TESTED
      if (b.getClearSelection()) clearSelection();
      FormControlModel model = b.getModel();
      Iterator iter = viewDescriptors.iterator();
      int index = 0;
      while (iter.hasNext())
      {
        OneFormControlLineView view = ((ViewDescriptor)iter.next()).view;
        if (view.getModel() == model)
        {
          Integer I = new Integer(index);
          int state = b.getState();
          if (state == 0) //toggle
            state = selection.contains(I) ? -1 : 1;
            
          switch (state)
          {
            case -1: //abw�hlen
                     ((ViewDescriptor)viewDescriptors.get(index)).view.unmark();
                     selection.remove(new Integer(index));
                     break;
            case 1: //ausw�hlen
                     if (!selection.contains(I)) 
                     {
                       ((ViewDescriptor)viewDescriptors.get(index)).view.mark();
                       selection.add(I);
                     }
                     break;
          }
        }
        
        ++index;
      }
      
      Collections.sort(selection);
    }
  }
  
  /**
   * Ein Eintrag in der Liste {@link AllFormControlModelLineViewsPanel#viewDescriptors}.
   *
   * @author Matthias Benkmann (D-III-ITD 5.1)
   */
  private static class ViewDescriptor
  {
    /**
     * Die View des Elements.
     */
    public OneFormControlLineView view;
    
    /**
     * Der Index des Elements innerhalb seines Tabs.
     */
    public int gridY;
    
    /**
     * Der Index des Tabs in {@link AllFormControlModelLineViewsPanel#tabPane}.
     */
    public int containingTabIndex;
    
    /**
     * true gdw das Element ein Tab ist.
     */
    public boolean isTab;
    
    /**
     * Erzeugt einen neuen ViewDescriptor.
     * @author Matthias Benkmann (D-III-ITD 5.1)
     */
    public ViewDescriptor(OneFormControlLineView view, int gridY, int containingTabIndex, boolean isTab)
    {
      this.view = view;
      this.gridY = gridY;
      this.containingTabIndex = containingTabIndex;
      this.isTab = isTab;
    }
  }
    
}
