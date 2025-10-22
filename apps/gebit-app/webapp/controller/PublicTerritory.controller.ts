import MultiInput, { MultiInput$TokenUpdateEvent } from "sap/m/MultiInput";
import Event from "sap/ui/base/Event";
import { ObjectBindingInfo } from "sap/ui/base/ManagedObject";
import Controller from "sap/ui/core/mvc/Controller";
import Filter from "sap/ui/model/Filter";
import FilterOperator from "sap/ui/model/FilterOperator";
import ODataListBinding from "sap/ui/model/odata/v4/ODataListBinding";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import Sorter from "sap/ui/model/Sorter";
/**
 * @namespace ui5.gebit.app.controller
 */
export default class PublicTerritory extends Controller {

	private lastUpdate: number = 0;

	public onInit() : void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "publicSearching") {
			this.matched(oEvent.getParameter("arguments").id);
		}
	}

	public matched(context:string) {
		let oBindingInfo = {} as ObjectBindingInfo;
		oBindingInfo.path = "/PublicTerritoryAssignments(" + context + ")";
		oBindingInfo.model = "publicService";
		(this.getView() as any).bindElement(oBindingInfo);
		
	}

	public async onUpdateMultiValueUpdate(oEvent: MultiInput$TokenUpdateEvent) {
		if(oEvent.getParameter("type") == "removed") {
			let removedObject = oEvent.getParameter("removedTokens")[0].getBindingContext("publicService").getObject();
			let oModel = this.getView().getModel("publicService") as ODataModel;
			await oModel.delete("/InWorkBy(" + removedObject.id + ")");
		}
	}

	public async onSubmitToken(oEvent: Event) {
		let mif = oEvent.getSource() as MultiInput;
		oEvent.getSource().setValue("");

		const ms = Date.now() - this.lastUpdate;
		if(ms < 1000) {
			console.log("Too fast, ignoring update");
			this.lastUpdate = undefined;
			return;
		}
		
		mif.getBinding("tokens").create({
			"freestyleName": oEvent.getParameter("value"),
		}, false,true);
		this.lastUpdate = Date.now();
	}

	public onUpdateMultiValueChange(oEvent: Event) {
		console.log("onUpdateMultiValueChange", oEvent);
	}

	public async onFilterSelect (oEvent:Event) {
		
		let sKey = oEvent.getParameter("key");
		let aFilters = [];
		let oBinding = this.getView()?.byId("groupTerrirtoryParts")?.getBinding("items") as ODataListBinding;
		let isNotDone = new Filter([new Filter("isDone", FilterOperator.EQ, null), new Filter("isDone", FilterOperator.EQ, false)], false);
		switch (sKey) {
			case "All":
			case "CloseToMe":
				break;
			
			case "Available":
				let inWorkByFilterEqNull = new Filter({'path':'inWorkBy', 'operator': FilterOperator.All, 'variable': 'item', 'condition': new Filter({
					'path':'item/id',
					'operator': FilterOperator.EQ,
					'value1': null
				})})
				aFilters.push(new Filter([ inWorkByFilterEqNull, isNotDone], true));
				break;
	
			case "InProgress": 
				let inWorkByFilter = new Filter({'path':'inWorkBy', 'operator': FilterOperator.Any, 'variable': 'item', 'condition': new Filter({
					'path':'item/id',
					'operator': FilterOperator.NE,
					'value1': null
				})})
				let isAssigned = new Filter([inWorkByFilter, isNotDone], true);
				aFilters.push(isAssigned);
				break;
			default:
				break;
		}

		oBinding.filter(new Filter(aFilters));
		oBinding.sort([new Sorter("name", false)]);
		


	}

}