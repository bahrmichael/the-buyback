import { BaseEntity } from './../../shared';

export class CapConfig implements BaseEntity {
    constructor(
        public id?: string,
        public typeId?: number,
        public typeName?: string,
        public price?: number,
        public deliveryLocation1?: string,
        public deliveryPrice1?: number,
        public deliveryLocation2?: string,
        public deliveryPrice2?: number,
        public deliveryLocation3?: string,
        public deliveryPrice3?: number,
        public deliveryLocation4?: string,
        public deliveryPrice4?: number,
    ) {
    }
}
