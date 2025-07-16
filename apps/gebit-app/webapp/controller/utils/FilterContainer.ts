import Controller from "sap/ui/core/mvc/Controller";
import Filter from "sap/ui/model/Filter";
import FilterOperator from "sap/ui/model/FilterOperator";

/**
 * @namespace ui5.gebit.app.controller.utils
 */           
export default class TerritoryFilterContainer {
    private searchFilter: Filter | null;
    private iconTabFilter: Filter | null;
    
    constructor(){
    }

    public addSearchFilter(queryString: string)  {
        this.buildSearchFieldFilters(queryString);
    }

    public addIconTabFilter(key:string) {
		switch (key) {
			case "All":
                this.searchFilter = null;
                this.iconTabFilter = null;
                break;
			case "Available":
                this.iconTabFilter =new Filter( [new Filter("toTerritoryAssignmentId", FilterOperator.EQ, null), new Filter("assignedUnregisteredUserAssignmentId", FilterOperator.EQ, null), new Filter("isReady", FilterOperator.EQ, true)], true);
                break;
			case "Assigned": 
                this.iconTabFilter =new Filter( [new Filter("toTerritoryAssignmentId", FilterOperator.NE, null), new Filter("assignedUnregisteredUserAssignmentId", FilterOperator.NE, null)], false);
				break;
			case "NotReady": 
                this.iconTabFilter =new Filter("isReady", FilterOperator.EQ, false);
				break;
			default:
				break;
		}
    }

    public getResultingFilters(): Filter {
        let result = [] as Filter[];
        
        if(this.searchFilter) {
            result.push(this.searchFilter)
        }

        if(this.iconTabFilter) {
            result.push(this.iconTabFilter)
        }
        return new Filter(result, true);
    }

    private buildSearchFieldFilters(sQuery:string) {
        let result = [] as Filter[];
        result.push(new Filter("name", FilterOperator.Contains, sQuery));
        result.push(new Filter("assignedToName", FilterOperator.Contains, sQuery));
        result.push(new Filter("assignedToSurname", FilterOperator.Contains, sQuery));
        result.push(new Filter("siteName", FilterOperator.Contains, sQuery));
        result.push(new Filter("siteDescription", FilterOperator.Contains, sQuery));
        result.push(new Filter("assignedUnregisteredUser", FilterOperator.Contains, sQuery));
        result.push(new Filter("assignedUnregisteredUserEmail", FilterOperator.Contains, sQuery));
        result.push(new Filter("siteName", FilterOperator.Contains, sQuery));
        this.searchFilter = new Filter(result, false);
    }
}