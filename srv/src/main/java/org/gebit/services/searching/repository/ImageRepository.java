package org.gebit.services.searching.repository;

import java.util.Optional;

import org.gebit.gen.db.Image;
import org.gebit.gen.db.Image_;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.services.persistence.PersistenceService;

@Component
public class ImageRepository {

	private PersistenceService ps;

	public ImageRepository(PersistenceService ps) {
		this.ps = ps;
	}
	
	public Optional<Image> byAssignmentId(String assignmentId) {
		return ps.run(Select.from(Image_.class).where(p -> p.toParent_ID().eq(assignmentId))).first(Image.class);
	}

	
	public void create(String imageUrl, String spacePath, String medyaType, String parentId) {
		Image image = Image.create();
		image.setImageUrl(imageUrl);
		image.setMediaType(medyaType);
		image.setSpacePath(spacePath);
		image.setToParentId(parentId);
		ps.run(Insert.into(Image_.class).entry(image));
	}
	
	public void deleteByAssignmentId(String assignmentId) {
		ps.run(Delete.from(Image_.class).where(p->p.toParent_ID().eq(assignmentId)));
	}

	public void deleteById(String id) {
		ps.run(Delete.from(Image_.class).byId(id));
	}
	
}
