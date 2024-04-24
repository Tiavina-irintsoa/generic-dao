package model.mapping;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) // durée de vie ( execution )
@Target(ElementType.FIELD)  //ne peut être utilisée que sur des champs de classe.
public @interface Column {
  String name() default "";
  String type() default "";
  boolean primary_key() default false;
}
