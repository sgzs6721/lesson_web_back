package com.lesson.vo.campus;

import com.lesson.repository.tables.records.EduCampusRecord;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-02T22:05:10+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 1.8.0_421 (Oracle Corporation)"
)
public class CampusVOConverterImpl implements CampusVOConverter {

    @Override
    public CampusVO toVO(EduCampusRecord record) {
        if ( record == null ) {
            return null;
        }

        CampusVO campusVO = new CampusVO();

        campusVO.setId( record.getId() );
        campusVO.setName( record.getName() );
        campusVO.setAddress( record.getAddress() );
        campusVO.setCreatedAt( record.getCreatedAt() );
        campusVO.setUpdatedAt( record.getUpdatedAt() );

        campusVO.setStatus( record.getStatus() == 1 );

        return campusVO;
    }

    @Override
    public List<CampusVO> toVOList(List<EduCampusRecord> records) {
        if ( records == null ) {
            return null;
        }

        List<CampusVO> list = new ArrayList<CampusVO>( records.size() );
        for ( EduCampusRecord eduCampusRecord : records ) {
            list.add( toVO( eduCampusRecord ) );
        }

        return list;
    }

    @Override
    public EduCampusRecord toRecord(CampusCreateVO vo) {
        if ( vo == null ) {
            return null;
        }

        EduCampusRecord eduCampusRecord = new EduCampusRecord();

        eduCampusRecord.setContactPhone( vo.getPhone() );
        eduCampusRecord.setName( vo.getName() );
        eduCampusRecord.setAddress( vo.getAddress() );
        eduCampusRecord.setContactName( vo.getContactName() );

        eduCampusRecord.setStatus( (byte) 1 );
        eduCampusRecord.setInstitutionId( (long) 1L );

        return eduCampusRecord;
    }

    @Override
    public EduCampusRecord toRecord(CampusUpdateVO vo) {
        if ( vo == null ) {
            return null;
        }

        EduCampusRecord eduCampusRecord = new EduCampusRecord();

        eduCampusRecord.setId( vo.getId() );
        eduCampusRecord.setName( vo.getName() );
        eduCampusRecord.setAddress( vo.getAddress() );

        return eduCampusRecord;
    }
}
