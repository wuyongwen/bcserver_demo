package com.cyberlink.cosmetic.modules.product.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import com.cyberlink.core.model.AbstractCoreEntity;


@Entity
@Table(name = "BC_PRODUCT_PRODUCTEFFECT")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class ProductProductEffect extends AbstractCoreEntity<Long>{

    private static final long serialVersionUID = 2425067524116128583L;
    private Long id;
	private Product product;
	private ProductEffect productEffect;

	@Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    
    @ManyToOne
    @JoinColumn(name = "EFFECT_ID")
    public ProductEffect getProductEffect() {
        return this.productEffect;
    }

    public void setProductEffect(ProductEffect productEffect) {
        this.productEffect = productEffect;
    }
}
