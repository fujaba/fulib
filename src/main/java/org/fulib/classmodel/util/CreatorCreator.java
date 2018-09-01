package org.fulib.classmodel.util;

import de.uniks.networkparser.IdMap;

class CreatorCreator{

   public static IdMap createIdMap(String session)
   {
      IdMap jsonIdMap = new IdMap().withSession(session);
      jsonIdMap.with(new AssocRoleCreator());
      jsonIdMap.with(new AssocRolePOCreator());
      jsonIdMap.with(new AttributeCreator());
      jsonIdMap.with(new AttributePOCreator());
      jsonIdMap.with(new ClassModelCreator());
      jsonIdMap.with(new ClassModelPOCreator());
      jsonIdMap.with(new ClazzCreator());
      jsonIdMap.with(new ClazzPOCreator());
      jsonIdMap.with(new FileFragmentMapCreator());
      jsonIdMap.with(new FileFragmentMapPOCreator());
      jsonIdMap.with(new CodeFragmentCreator());
      jsonIdMap.with(new CodeFragmentPOCreator());
      return jsonIdMap;
   }
}
