
package cz.dynawest.ibatis.beans;

/**
 *
 * @author ondra
 */
public class MapEntry {
  String table, name, value;

  public MapEntry(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public MapEntry(String table, String name, String value) {
    this.table = table;
    this.name = name;
    this.value = value;
  }
}
