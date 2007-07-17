package ao.holdem.history.persist;

import ao.util.serial.Stringer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * 
 */
@MappedSuperclass
public class Base implements Serializable
{
    //----------------------------------------------------------
    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId()        { return id;    }
    public void setId(Long id) { this.id = id; }


    //----------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Base base = (Base) o;
        if (id != null && base.id != null)
        {
            return id.equals( base.id );
        }
        return id      == null &&
               base.id == null &&
               this    == base;
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
