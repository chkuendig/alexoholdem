package ao.holdem.engine.persist;

import ao.util.serial.Stringer;

//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * 
 */
//@MappedSuperclass
public class Base implements Serializable
{
    //----------------------------------------------------------
    private Long id;

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId()        { return id;    }
    public void setId(Long id) { this.id = id; }


    //----------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        // getClass() != o.getClass() doesn't work
        //  due to CGLib instrumentation by Hibernate.
        if (o == null || !(o instanceof Base)) return false;

        Base base = (Base) o;
        return getId() != null && base.getId() != null &&
               getId().equals(base.getId());
    }

    @Override
    public String toString()
    {
        return Stringer.toString( this );
    }

    @Override
    public int hashCode()
    {
        return id.intValue();
    }
}
