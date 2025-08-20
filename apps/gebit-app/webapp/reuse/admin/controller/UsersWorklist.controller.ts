import { ListItemBase$PressEvent } from "sap/m/ListItemBase";
import { SearchField$ChangeEvent, SearchField$SearchEvent } from "sap/m/SearchField";
import Controller from "sap/ui/core/mvc/Controller";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import UIComponent from "sap/ui/core/UIComponent";
import Binding from "sap/ui/model/Binding";
import Filter from "sap/ui/model/Filter";
import FilterOperator from "sap/ui/model/FilterOperator";
import Context from "sap/ui/model/odata/v4/Context";

/**
 * @namespace ui5.gebit.app.reuse.admin.controller
 */           
export default class UsersWorklist extends Controller {

	public onInit() : void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "users") {
			this.matched();
		}
	}

	public matched() {
		const binding = this.byId("usersTable")?.getBinding("items") as Binding | undefined;
		if (binding) {
			binding.refresh();
		}
	}

   
	public onPressListItem(oEvent: ListItemBase$PressEvent) {
		let oItem = oEvent.getSource();
		let oContext = oItem.getBindingContext() as Context;
		let sPath = oContext.getPath();
		let userId = sPath.split("(")[1].split(")")[0];
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.navTo("user", {userId: userId});
	}

	public onSearch(oEvent: SearchField$SearchEvent) {
		let sQuery = oEvent.getParameter("query");
		let oTable = this.byId("usersTable");

		let result = [] as Filter[];
        result.push(new Filter("name", FilterOperator.Contains, sQuery));
        result.push(new Filter("surname", FilterOperator.Contains, sQuery));
        result.push(new Filter("email", FilterOperator.Contains, sQuery));
        let searchFilter = new Filter(result, false);
		
		if (oTable) {
			let oBinding = oTable.getBinding("items");
			if (oBinding) {
				oBinding.filter(sQuery ? searchFilter : []);
			}
		}	
	}
	
}