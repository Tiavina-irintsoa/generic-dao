package model.mapping;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import connexion.Connect;

/// stream() renvoie un objet java.util.stream.Stream qui représente une suite d'éléments
/// map est une méthode de la classe java.util.stream.Stream en Java. 
/// Elle prend en argument une fonction qui définit comment les éléments d'un stream doivent être transformés.
/// <T> est un paramètre de type générique en Java. Les types génériques permettent de spécifier un ou plusieurs types qui seront déterminés lors de l'instanciation de la classe ou de la méthode

public class BddToObject{

  static PreparedStatement prepareStatementUpdate(Connection connection , List<Field> columnFields , String sql , Object object , int start  , PreparedStatement statement)throws Exception{     //préparation de la requete pour insert et update
/// initialisation
    if( start == 0 )  statement = connection.prepareStatement(sql);
    Object value ;   
    for (int i = start; i < columnFields.size() + start ; i++) {
      Field field = columnFields.get(i - start );
      field.setAccessible(true);
      value = field.get(object);
      System.out.println(value);
      statement.setObject( i + 1   , value);
    }
    return statement;
  }

/// update
  static String getSetSqlUpdate( String sql , List<Field> columnFields2 , String tempo )throws Exception{          //update tab set ...
    StringJoiner joiner = new StringJoiner(" , ");
    if( columnFields2.size() == 0 ) throw new Exception(" aucun changement pour udpate ");
    for (Field field : columnFields2) {
      tempo = " %s = ? ";
      Column column = field.getAnnotation(Column.class);

      joiner.add( String.format( tempo , column.name()) );
    }
    return joiner.toString();
  }

  static String getSqlUpdate( String table , List<Field> columnFields1 , List<Field> columnFields2)throws Exception{                 //la requete pour update
    String sql , where , set , tempo = "" ;  StringJoiner joiner = new StringJoiner(" AND ");
      sql = "UPDATE %s SET %s ";
      set = getSetSqlUpdate(sql, columnFields2 , tempo);
      if( columnFields1.size() == 0 ){
        sql = String.format( sql , table , set );  
      }else{
        sql += " WHERE %s ";
        for (Field field : columnFields1) {
          tempo = " %s = ? ";
          Column column = field.getAnnotation(Column.class);

          joiner.add( String.format( tempo , column.name()) );
        }
        where  = joiner.toString();
        sql = String.format( sql , table , set , where );
      }
      return sql;
  }

/// update
  public static void update( Connect c , Object object1 , Object object2 )throws Exception{
    /// initialisation 
    Connection connection = null;   PreparedStatement statement = null;   boolean transaction = true;
    Vector result = new Vector<>();
    String sql , table;
    List<Field> columnFields1 ,columnFields2 ;
    Class<?> clazz; Field[] fields;
    String tempo = "";  int start = 0;
    if( c == null  ){
      transaction = false;
      c = new Connect();
      c.getConnectionPostGresql();
    }
      try {
        connection = c.getConnection();
        clazz = object1.getClass();
        fields = clazz.getDeclaredFields();
        table = toLowerCaseFirstELement( getTableName(clazz) );
        
        //les attributs correspondant a une colonne
        columnFields1 = getField(fields);
        columnFields2 = getField(fields);

        //les attributs non null
        columnFields1 = fieldNotNull(object1, columnFields1);
        columnFields2 = fieldNotNull(object2, columnFields2);

        //requete sal
        sql = getSqlUpdate(table, columnFields1, columnFields2);
        System.out.println(sql);

        //récuperer le preparedStatement
        statement = prepareStatementUpdate(connection, columnFields2, sql, object2, start , statement);
        start = columnFields2.size();
        statement = prepareStatementUpdate(connection, columnFields1, sql, object1, start , statement);

        //execution
        statement.executeUpdate();
        if( transaction != true ){
          connection.commit();
        }
      } catch (IllegalAccessException | SQLException e) {
        connection.rollback();
        if( statement != null ){
          statement.close();
        }           
        if( connection != null ){
          connection.close();
        }
        throw e;
      } finally {
        try {
          if( transaction != true ){
            if( statement != null ){
              statement.close();
            }           
            if( connection != null ){
              connection.close();
            }
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
  }

/// delete 
  public static void  delete( Connect c , Object object )throws Exception{
    /// initialisation 
    Connection connection = null;   PreparedStatement statement = null;   boolean transaction = true;
    Vector result = new Vector<>();
    String sql , table;
    List<Field> columnFields;
    Class<?> clazz; Field[] fields;
    String tempo = "";
    if( c == null  ){
      transaction = false;
      c = new Connect();
      c.getConnectionPostGresql();
    }
      try {
        connection = c.getConnection();
        clazz = object.getClass();
        fields = clazz.getDeclaredFields();
        table = toLowerCaseFirstELement( getTableName(clazz) );
        
        //les attributs correspondant a une colonne
        columnFields = getField(fields);

        //les attributs non null
        columnFields = fieldNotNull(object, columnFields);

        //la requete sql delete from ...
        sql = getSql(table, columnFields , "delete");
        System.out.println(sql);

        //récupérer les valeurs de prearedstatement
        statement = preparedStatementSelect(statement, connection, sql, columnFields, object);

        statement.executeUpdate();
        if( transaction != true ){
          connection.commit();
        }

      } catch (IllegalAccessException | SQLException e) {
        connection.rollback();
        if( statement != null ){
          statement.close();
        }           
        if( connection != null ){
          connection.close();
        }
        throw e;
      } finally {
        try {
          if( transaction != true ){
            statement.close();
            connection.close();
          }
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
  }

/// select 
  static Vector getResult( Vector result , PreparedStatement statement ,  List<Field> columnFields , Object object )throws Exception{ //récupération en objet des valeurs de la requete
    try (ResultSet resultSet = statement.executeQuery()) {
      while (resultSet.next()) {
        Object entity = object.getClass().getDeclaredConstructor().newInstance();
          for (Field field : columnFields) {
            //appel du setter de l'attribut
            System.out.println( resultSet.getObject(getColumnName(field)).getClass().getName() );
            String methodName = getSetterName( field.getName() );
            entity = callMethodByName( entity , methodName , String.class ,  resultSet.getString( getColumnName(field) ));
          }
          result.add(entity);
      }
    }
    return result;
  }

  static PreparedStatement preparedStatementSelect( PreparedStatement statement , Connection connection , String sql , List<Field> columnFields , Object object )throws Exception{                                                            //préparation statemnent select
    int i = 1;
    statement = connection.prepareStatement(sql);
    for (Field field : columnFields) {
        System.out.println( field.get(object) );
        System.out.println( "i: "+ i);
        statement.setObject(i, field.get(object));
        i++;
    }
    return statement;
  }

  static List<Field> fieldNotNull( Object object , List<Field> columnFields )throws Exception{                    //récupérer les valeurs non null
    Vector<Field> fields = new Vector<Field>(); List<Field> list;
    for (Field field : columnFields) {
      field.setAccessible(true);
      if( field.get(object) != null ){
          fields.add(field);
      }
    }
    list = new ArrayList<Field>( fields );
    return list;
  }

  static String getSql( String table , List<Field> columnFields , String  action )throws Exception{                 //la requete pour select
    String sql , where  , tempo;  StringJoiner joiner = new StringJoiner(" AND ");
      sql = "SELECT * FROM %s ";
        if( action.compareToIgnoreCase("delete") == 0 ){
          sql = " DELETE FROM %s ";
        }
      if( columnFields.size() == 0 ){
        sql = String.format( sql , table);
      }else{
        sql += " WHERE %s ";
        for (Field field : columnFields) {
          tempo = " %s = ? ";
          Column column = field.getAnnotation(Column.class);

          joiner.add( String.format( tempo , column.name()) );
        }
        where  = joiner.toString();
        sql = String.format( sql , table , where );
      }
      return sql;
  }

  public static Object findById( Connect c , Object object )throws Exception{
    boolean transaction = true;
    Object value = null;
    if( c == null  ){
      transaction = false;
      c = new Connect();
      c.getConnectionPostGresql();
    }
    try {
      Vector list = select(c, object);
      if( list.size() != 0 ){
        value = list.get(0);
      }
      return list;
    } catch (Exception e) {
      c.getConnection().rollback();
      throw e;
    }finally {
      try {
        if( transaction != true ){
          c.getConnection().close();
        }
      } catch (SQLException e) {
          throw e;
      }
    }

  }


  public static Vector select(  Connect c ,  Object object )throws Exception{
/// initialisation 
    Connection connection = null;   PreparedStatement statement = null;   boolean transaction = true;
    Vector result = new Vector<>();
    String sql , table;
    List<Field> columnFields;
    Class<?> clazz; Field[] fields;
    String tempo = "";
    if( c == null  ){
      transaction = false;
      c = new Connect();
      c.getConnectionPostGresql();
    }
      try {
        connection = c.getConnection();
        clazz = object.getClass();
        fields = clazz.getDeclaredFields();
        table = toLowerCaseFirstELement( getTableName(clazz) );
        
        //les attributs correspondant a une colonne
        columnFields = getField(fields);

        //les attributs non null
        columnFields = fieldNotNull(object, columnFields);

        //la requete sql select * from ...
        sql = getSql(table, columnFields , "select");
        System.out.println(sql);

        //donner les valeurs au prearedstatement
        statement = preparedStatementSelect(statement, connection, sql, columnFields, object);

        columnFields = getField(fields);

        //récupérer les valeurs en Objet de la requete
        result = getResult(result, statement, columnFields, object);

      return result;

      } catch (IllegalAccessException | SQLException e) {
        connection.rollback();
        if( statement != null ){
          statement.close();
        }           
        if( connection != null ){
          connection.close();
        }
        throw e;
      } finally {
        try {
          if( transaction != true ){
            if( statement != null ){
              statement.close();
            }           
            if( connection != null ){
              connection.close();
            }
          }
        } catch (SQLException e) {
          throw e;
        }
      }
  }

/// pour setter
    public static String getSetterName( String value )throws Exception{               //récupérer nom du setter
      String set , method;
      set = "set";
      method = toUpperCaseFirstELement(value);
      return set + method;
    }

    public static <T> Object callMethodByName(Object object, String methodName ,  Class<T> parameterType, T parameterValue) throws Exception {
      Method method = object.getClass().getMethod(methodName, parameterType);
      method.invoke(object, parameterValue);
      return object;
    }

/// insert
    static String getColumnName(Field field){                                    //connaitre le nom de la colonne par rapport a l'attribut
      Column column = field.getAnnotation(Column.class);
      return column.name().isEmpty() ? field.getName() : column.name();
    }

    static List<Field> getFieldWithoutPrimary( List<Field> fields ){            //colonne sans la clé primaire ( pour insertion )
      List<Field> columnFields = new ArrayList<>(); Column column;
      for (Field field : fields) {
          column = field.getAnnotation(Column.class);
          if( column.primary_key() == false )
            columnFields.add(field);
      }
      return columnFields;
    }

    static List<Field> getField( Field[] fields ) throws Exception {                                //prend les attributs qui correspondent a une colonne
        List<Field> columnFields = new ArrayList<>();
            
        for (Field field : fields) {
          if (field.isAnnotationPresent(Column.class)) {
            columnFields.add(field);
          }
        }
        return columnFields;
    }

    static String toUpperCaseFirstELement( String original )throws Exception{                                             //premier element en majuscule
      return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    static String toLowerCaseFirstELement( String original )throws Exception{                                             //premier element en minuscule
      return original.substring(0, 1).toLowerCase() + original.substring(1);
    }

    static String getTableName( Class<?> clazz )throws Exception{
      Table table = clazz.getAnnotation(Table.class); 
      return table.name();
    }

    public static void insert( Connect c , Object object)throws Exception {
/// initialisation 
        Connection connection = null;   PreparedStatement statement = null;   boolean transaction = true;
        String sql, columns , values , table;
        List<Field> columnFields;
        Class<?> clazz; Field[] fields;

        if( c == null  ){
          transaction = false;
          c = new Connect();
          c.getConnectionPostGresql();
        }
          try {
            connection = c.getConnection();
            clazz = object.getClass();
            fields = clazz.getDeclaredFields();
            table = toLowerCaseFirstELement(getTableName(clazz));
            
            //les attributs correspondant a une colonne
            columnFields = getField(fields);

            //les attributs qui ne sont pas des clés primaires
            columnFields = getFieldWithoutPrimary( columnFields );

            //insert into ( ... , ... , ...  )
            columns = String.join(", ", columnFields.stream().map(BddToObject::getColumnName).toArray(String[]::new));   

            //values ( ? , ? , ... , ? )
            values = String.join(", ", Collections.nCopies(columnFields.size(), "?"));
            
            sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, columns, values);
            System.out.println(sql);
            
            //préparation de la requete
            statement = prepareStatementUpdate(connection, columnFields, sql, object , 0 , statement);
            
            statement.executeUpdate();
            if( transaction != true ){
              connection.commit();
            }
          } catch (IllegalAccessException | SQLException e) {
            connection.rollback();
            if( statement != null ){
              statement.close();
            }           
            if( connection != null ){
              connection.close();
            }
            throw e;
          } finally {
            try {
              if( transaction != true ){
                if( statement != null ){
                  statement.close();
                }           
                if( connection != null ){
                  connection.close();
                }
              }
            } catch (SQLException e) {
              e.printStackTrace();
            }
          }
    }
}