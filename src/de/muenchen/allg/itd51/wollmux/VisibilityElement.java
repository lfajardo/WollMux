/*
 * Dateiname: VisibilityElement.java
 * Projekt  : WollMux
 * Funktion : Beschreibt ein Sichtbarkeitselement, das gesteuert �ber Sichtbarkeitsgruppen
 *            auf "sichtbar" oder "unsichtbar" geschalten werden kann.
 * 
 * Copyright: Landeshauptstadt M�nchen
 *
 * �nderungshistorie:
 * Datum      | Wer | �nderungsgrund
 * -------------------------------------------------------------------
 * 02.01.2007 | LUT | Erstellung als VisibilityElement
 * -------------------------------------------------------------------
 *
 * @author Christoph Lutz (D-III-ITD 5.1)
 * @version 1.0
 * 
 */
package de.muenchen.allg.itd51.wollmux;

import java.util.Set;

import com.sun.star.text.XTextRange;

/**
 * Dieses Interface beschreibt ein Sichtbarkeitselement, das gesteuert �ber sog.
 * Sichtbarkeitsgruppen sichtbar oder unsichtbar geschalten werden kann.
 * 
 * Derzeit wird das Interface von folgenden Klassen implementiert:
 * DocumentCommand und TextSection
 * 
 * @author christoph.lutz
 */
public interface VisibilityElement
{

  /**
   * gibt den Sichtbarkeitsstatus des Sichtbarkeitselements zur�ck.
   * 
   * @return true=sichtbar, false=ausgeblendet
   */
  public abstract boolean isVisible();

  /**
   * Setzt den Sichtbarkeitsstatus des Elements.
   * 
   * @param visible
   *          true=sichtbar, false=ausgeblendet
   */
  public abstract void setVisible(boolean visible);

  /**
   * Liefert alle Sichtbarkeitsgruppen zu diesem Sichtbarkeitselement.
   * 
   * @return Ein Set, das alle zugeordneten groupId's als Strings enth�lt.
   */
  public abstract Set getGroups();

  /**
   * f�gt diesem Elements all in groups definierten Sichtbarkeitsgruppen hinzu.
   */
  public abstract void addGroups(Set groups);

  /**
   * Liefert die TextRange an der das VisibleElement verankert ist oder null,
   * falls das VisibleElement nicht mehr existiert.
   */
  public abstract XTextRange getAnchor();

}
