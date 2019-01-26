import { BaseEntity } from './../../shared';

export const enum CapOrderStatus {
    'REQUESTED',
    'INBUILD',
    'COMPLETED'
}

export class CapOrder implements BaseEntity {
    constructor(
        public id?: string,
        public recipient?: string,
        public typeId?: number,
        public typeName?: string,
        public price?: number,
        public created?: any,
        public deliveryLocation?: string,
        public deliveryPrice?: number,
        public status?: CapOrderStatus,
    ) {
    }
}
